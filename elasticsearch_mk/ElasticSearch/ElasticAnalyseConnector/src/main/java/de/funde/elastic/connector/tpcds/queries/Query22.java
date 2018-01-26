package de.funde.elastic.connector.tpcds.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.main.ElasticAnalyseConnector;


/**
 * select  i_product_name
             ,i_brand
             ,i_class
             ,i_category
             ,avg(inv_quantity_on_hand) qoh
       from inventory
           ,date_dim
           ,item
       where inv_date_sk=d_date_sk
              and inv_item_sk=i_item_sk
              and d_month_seq between 1212 and 1212 + 11
       group by rollup(i_product_name
                       ,i_brand
                       ,i_class
                       ,i_category)
order by qoh, i_product_name, i_brand, i_class, i_category
limit 100;
 * @author Marco
 *
 */
public class Query22 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query22.class);


	public Query22(RestHighLevelClient client) {
		this.client = client;
	}


	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {
			final StopWatch sw = new StopWatch("Query 22", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['inv_date_sk'].value ==  doc['d_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['inv_item_sk'].value ==  doc['i_item_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new RangeQueryBuilder("d_month_seq").from(1212).to(1212 + 11))
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	        );

			// SORT
			final List<SortBuilder<?>> sortBuilders = new ArrayList<>();
			sortBuilders.add(new FieldSortBuilder("i_product_name.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("i_brand.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("i_class.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("i_category.keyword").order(SortOrder.ASC));


			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("i_product_name")
			        .field("i_product_name.keyword");//.order(Order.aggregation("i_brand>i_class>i_category>qoh", "avg", true));
			aggregation.subAggregation(AggregationBuilders.terms("i_brand")
					        .field("i_brand.keyword")
					        .subAggregation(AggregationBuilders.terms("i_class")
					        		.field("i_class.keyword")
					        		.subAggregation(AggregationBuilders.terms("i_category")
					        				.field("i_category.keyword")
					        				.order(Order.aggregation("qoh", true))
											.subAggregation(AggregationBuilders.avg("qoh")
									        		.field("inv_quantity_on_hand"))
											.subAggregation(AggregationBuilders.topHits("additionalValues")
													.fetchSource(new String[] {"i_product_name", "i_brand", "i_class", "i_category"}, new String[0])
													.sorts(sortBuilders)))));

			sourceBuilder.aggregation(aggregation);

			// LIMIT
			sourceBuilder.from(0);
			sourceBuilder.size(100);

			final SearchRequest searchRequest = new SearchRequest("invtest");
			searchRequest.source(sourceBuilder);
			searchRequest.types("doc");
			sw.beforeExecution();
			final SearchResponse searchResponse = this.client.search(searchRequest);
			sw.afterExecution();
			sw.setTookInMillis(searchResponse.getTookInMillis());
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
				final Terms byProductNameAggregation = aggregations.get("i_product_name");
				for (final Bucket b : byProductNameAggregation.getBuckets()) {
					final Terms byBrandAggregation = b.getAggregations().get("i_brand");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final Terms byClassAggregation = b1.getAggregations().get("i_class");
						for (final Bucket b2 : byClassAggregation.getBuckets()) {
							final Terms byCategoryAggregation = b2.getAggregations().get("i_category");
							for (final Bucket b3 : byCategoryAggregation.getBuckets()) {
								final StringBuilder builder = new StringBuilder();
								final Avg average = b3.getAggregations().get("qoh");
								final TopHits topHits = b3.getAggregations().get("additionalValues");
							    for (final SearchHit hit : topHits.getHits().getHits()) {
							        builder.append(hit.getSourceAsString());
							    }
								LOG.debug("Name: {}  \n  -  AVG: {} \n  -  {} ", b3.getKeyAsString(), average.getValue(), builder.toString());
							}
						}
					}
				}
			}
			sw.afterResultPrinting();
			return Optional.of(sw);
		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
		return Optional.empty();
	}
}
