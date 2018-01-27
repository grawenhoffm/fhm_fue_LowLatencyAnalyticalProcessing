package de.funde.elastic.connector.tpcds.queries.messungen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
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
 * select  i_brand_id brand_id, i_brand brand, i_manufact_id, i_manufact,
 	sum(ss_ext_sales_price) ext_price
 from date_dim, store_sales, item,customer,customer_address,store
 where d_date_sk = ss_sold_date_sk
   and ss_item_sk = i_item_sk
   and i_manager_id=7
   and d_moy=11
   and d_year=1999
   and ss_customer_sk = c_customer_sk
   and c_current_addr_sk = ca_address_sk
   and substr(ca_zip,1,5) <> substr(s_zip,1,5)
   and ss_store_sk = s_store_sk
 group by i_brand
      ,i_brand_id
      ,i_manufact_id
      ,i_manufact
 order by ext_price desc
         ,i_brand
         ,i_brand_id
         ,i_manufact_id
         ,i_manufact
limit 100 ;
 * @author Marco
 *
 */
public class Query19 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query19.class);


	public Query19(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {
		try {
			final StopWatch sw = new StopWatch("Query 19", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_item_sk'].value ==  doc['ss_item_i_item_sk'].value", new HashMap<>());
			final Script script2 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_customer_sk'].value ==  doc['ss_customer_c_customer_sk'].value", new HashMap<>());
			final Script script3 = new Script(ScriptType.INLINE, "painless", "doc['ss_customer_c_current_addr_sk'].value ==  doc['ss_c_current_addr_ca_address_sk'].value", new HashMap<>());
			final Script script4 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());
			final Script script5 = new Script(ScriptType.INLINE, "painless", "doc['ss_addr_ca_zip'].value.substring(5) ==  doc['ss_store_s_zip'].value.substring(5)", new HashMap<>());// TODO 	QueryBuilders.regexpQuery() benutzen um Substring richtig zu bilden

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(new ScriptQueryBuilder(script2))
	                .must( new ScriptQueryBuilder(script3))
	                .must(new ScriptQueryBuilder(script4))
	               // .must(new ScriptQueryBuilder(script5))

	                .must(QueryBuilders.termQuery("ss_item_i_manager_id", 7))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_moy", 11))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_year", 1999))
	        );

			// SORT
			final List<SortBuilder<?>> sortBuilders = new ArrayList<>();
			sortBuilders.add(new FieldSortBuilder("ss_item_i_brand.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_item_i_brand_id").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_item_i_manufact_id").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_item_i_manufact.keyword").order(SortOrder.ASC));

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("i_brand")
			        .field("i_brand.keyword");//.order(Order.aggregation("i_brand>i_class>i_category>qoh", "avg", true));
			aggregation.subAggregation(AggregationBuilders.terms("i_brand_id")
					        .field("ss_item_i_brand_id")
					        .subAggregation(AggregationBuilders.terms("i_manufact_id")
					        		.field("ss_item_i_manufact_id")
					        		.subAggregation(AggregationBuilders.terms("i_manufact")
					        				.field("ss_item_i_manufact.keyword")
					        				.order(Order.aggregation("ext_price", false))
											.subAggregation(AggregationBuilders.sum("ext_price")
									        		.field("ss_ss_ext_sales_price"))
											.subAggregation(AggregationBuilders.topHits("additionalValues")
													.fetchSource(new String[] {"ss_item_i_brand_id", "ss_item_i_brand", "ss_item_i_manufact_id", "ss_item_i_manufact"}, new String[0])
													.sorts(sortBuilders)))));

			sourceBuilder.aggregation(aggregation);

			// LIMIT
			sourceBuilder.from(0);
			sourceBuilder.size(100);

			final SearchRequest searchRequest = new SearchRequest(indexname);
			searchRequest.source(sourceBuilder);
			searchRequest.types(Constants.TPCDS_DOC_NAME);
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
				final Terms byProductNameAggregation = aggregations.get("i_brand");
				for (final Bucket b : byProductNameAggregation.getBuckets()) {
					final Terms byBrandAggregation = b.getAggregations().get("i_brand_id");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final Terms byClassAggregation = b1.getAggregations().get("i_manufact_id");
						for (final Bucket b2 : byClassAggregation.getBuckets()) {
							final Terms byCategoryAggregation = b2.getAggregations().get("i_manufact");
							for (final Bucket b3 : byCategoryAggregation.getBuckets()) {
								final StringBuilder builder = new StringBuilder();
								final Sum average = b3.getAggregations().get("ext_price");
								final TopHits topHits = b3.getAggregations().get("additionalValues");
							    for (final SearchHit hit : topHits.getHits().getHits()) {
							        builder.append(hit.getSourceAsString());
							    }
								LOG.debug("Name: {}  \n  -  SUM: {} \n  -  {} ", b3.getKeyAsString(), average.getValue(), builder.toString());
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
