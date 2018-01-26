package de.funde.elastic.connector.tpcds.queries.messungen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.connector.tpcds.queries.IQuery;
import de.funde.elastic.main.ElasticAnalyseConnector;

/**
 * select i_item_id
      ,i_item_desc
      ,i_category
      ,i_class
      ,i_current_price
      ,sum(ss_ext_sales_price) as itemrevenue
      ,sum(ss_ext_sales_price)*100/sum(sum(ss_ext_sales_price)) over // Division und verschachtelte Summe und over wird weggelassen
          (partition by i_class) as revenueratio
from
	store_sales
    	,item
    	,date_dim
where
	ss_item_sk = i_item_sk
  	and i_category in ('Jewelry', 'Sports', 'Books')
  	and ss_sold_date_sk = d_date_sk
	and d_date between cast('2001-01-12' as date)
				and (cast('2001-01-12' as date) + interval '30' day)
group by
	i_item_id
        ,i_item_desc
        ,i_category
        ,i_class
        ,i_current_price
order by
	i_category
        ,i_class
        ,i_item_id
        ,i_item_desc
        ,revenueratio;
 * @author Marco
 *
 */
public class Query98 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query98.class);


	public Query98(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {


		try {
			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			final StopWatch sw = new StopWatch("Query 98", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_item_sk'].value ==  doc['ss_item_i_item_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
//	                .must(new ScriptQueryBuilder(script))
//	                .must(new ScriptQueryBuilder(script1))
	                .must(QueryBuilders.boolQuery()
	                		.should(QueryBuilders.termQuery("ss_item_i_category.keyword", "Jewelry"))
	                		.should(QueryBuilders.termQuery("ss_item_i_category.keyword", "Sports"))
	                		.should(QueryBuilders.termQuery("ss_item_i_category.keyword", "Books"))
	                )
	                .must(new RangeQueryBuilder("ss_sold_date_d_date").from(dateFormat.parse("2001-01-12")).to(dateFormat.parse("2001-02-11")))

	        );

			// SORT
			final List<SortBuilder<?>> sortBuilders = new ArrayList<>();
			sortBuilders.add(new FieldSortBuilder("ss_item_i_category.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_item_i_class.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_item_i_item_id.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_item_i_item_desc.keyword").order(SortOrder.ASC));


			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("i_item_id")
			        .field("ss_item_i_item_id.keyword");
			aggregation.subAggregation(AggregationBuilders.terms("i_item_desc")
					        .field("ss_item_i_item_desc.keyword")
					        .subAggregation(AggregationBuilders.terms("i_category")
					        		.field("ss_item_i_category.keyword")
					        		.subAggregation(AggregationBuilders.terms("i_class")
							        		.field("ss_item_i_class.keyword")
							        		.subAggregation(AggregationBuilders.terms("i_current_price")
									        		.field("ss_item_i_current_price")
								        			.order(BucketOrder.aggregation("revenueratio", true))
													.subAggregation(AggregationBuilders.sum("itemrevenue")
												        .field("ss_ss_ext_sales_price"))
													.subAggregation(AggregationBuilders.sum("ss_ext_sales_price")
													        .field("ss_ss_ext_sales_price"))
													.subAggregation(AggregationBuilders.sum("revenueratio")
													        .field("ss_ss_ext_sales_price"))
													.subAggregation(AggregationBuilders.topHits("additionalValues")
															.fetchSource(new String[] {"ss_item_i_item_desc", "ss_item_i_category", "ss_item_i_class", "ss_item_i_current_price"}, new String[0])
															.sorts(sortBuilders))))));

			sourceBuilder.aggregation(aggregation);

			final SearchRequest searchRequest = new SearchRequest(indexname);
			searchRequest.source(sourceBuilder);
			searchRequest.types(Constants.TPCDS_DOC_NAME);
			sw.beforeExecution();
			final SearchResponse searchResponse = this.client.search(searchRequest);
			sw.afterExecution();
			sw.setTookInMillis(searchResponse.getTook().getMillis());
			sw.setTotalShards(searchResponse.getTotalShards());
			sw.setFailedShards(searchResponse.getFailedShards());
			sw.setSkippedShards(searchResponse.getSkippedShards());
			sw.setSuccessShards(searchResponse.getSuccessfulShards());
			sw.setNumberOfReducePhases(searchResponse.getNumReducePhases());
			if (ElasticAnalyseConnector.WITH_RESULT_LOGGING) {
				final StringBuffer sb = new StringBuffer();

				for (final SearchHit hit : searchResponse.getHits().getHits()) {
					sb.append("----------------------\n");
					sb.append("Id: ").append(hit.getId()).append("\n");
					sb.append(hit.getSourceAsString()).append("\n");

				}
				LOG.debug(sb.toString());


				// Aggregations Ergebnis abfragen
				final Aggregations aggregations = searchResponse.getAggregations();
				final Terms byProductNameAggregation = aggregations.get("i_item_id");
				for (final Bucket b : byProductNameAggregation.getBuckets()) {
					final Terms byBrandAggregation = b.getAggregations().get("i_item_desc");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final Terms byCategoryAggregation = b1.getAggregations().get("i_category");
						for (final Bucket b2 : byCategoryAggregation.getBuckets()) {
							final Terms byClassAggregation = b2.getAggregations().get("i_class");
							for (final Bucket b3 : byClassAggregation.getBuckets()) {
								final Terms byPriceAggregation = b3.getAggregations().get("i_current_price");
								for (final Bucket b4 : byPriceAggregation.getBuckets()) {
									final StringBuilder builder = new StringBuilder();
									final Sum sum = b4.getAggregations().get("itemrevenue");
									final Sum sum1 = b4.getAggregations().get("ss_ext_sales_price");
									final Sum sum2 = b4.getAggregations().get("revenueratio");
									final TopHits topHits = b4.getAggregations().get("additionalValues");
								    for (final SearchHit hit : topHits.getHits().getHits()) {
								        builder.append(hit.getSourceAsString());
								    }
									LOG.debug("Name: {}  \n  -  SUM1: {} \n  -  SUM2: {} \n  -  SUM3: {} \n  -  {} ", b4.getKeyAsString(), sum.getValue(), sum1.getValue(), sum2.getValue(), builder.toString());
								}
							}
						}
					}
				}
			}
			sw.afterResultPrinting();
			return Optional.of(sw);
		} catch (final Exception e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
		return Optional.empty();
	}

}
