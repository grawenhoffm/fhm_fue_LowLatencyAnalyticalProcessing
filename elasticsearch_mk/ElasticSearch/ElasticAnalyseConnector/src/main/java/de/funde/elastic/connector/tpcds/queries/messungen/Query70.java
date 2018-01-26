package de.funde.elastic.connector.tpcds.queries.messungen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.connector.tpcds.queries.IQuery;
import de.funde.elastic.main.ElasticAnalyseConnector;

/**
 * sum(ss_net_profit) as total_sum
   ,s_state
   ,s_county
 from
    store_sales
   ,date_dim       d1
   ,store
 where
    d1.d_month_seq between 1212 and 1212+11
 and d1.d_date_sk = ss_sold_date_sk
 and s_store_sk  = ss_store_sk
 and s_state in
             ( select s_state
               from  (select s_state as s_state
                      from   store_sales, store, date_dim
                      where  d_month_seq between 1212 and 1212+11
 			    and d_date_sk = ss_sold_date_sk
 			    and s_store_sk  = ss_store_sk
                      group by s_state
                     ) tmp1
             )
 group by s_state,s_county
 limit 100;
 * @author Marco
 *
 */
public class Query70 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query70.class);


	public Query70(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {

			// SubqueryResults
			final List<String> s_stateArray = new ArrayList<>();

			//
			// Erstes Subquery:
			//
			final Script scriptSub1 = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script scriptSub2 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());

			final SearchSourceBuilder sourceBuilderSubQuery1 = new SearchSourceBuilder();
			sourceBuilderSubQuery1.query(QueryBuilders.boolQuery()
					.must(new ScriptQueryBuilder(scriptSub1))
					.must(new ScriptQueryBuilder(scriptSub2))
	                .must(QueryBuilders.rangeQuery("ss_sold_date_d_month_seq").from(1212).to(1223))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_moy", 2))
	        );

			final TermsAggregationBuilder subQueryAggr = AggregationBuilders.terms("s_state")
			        .field("ss_store_s_state.keyword");
			sourceBuilderSubQuery1.aggregation(subQueryAggr);
			sourceBuilderSubQuery1.fetchSource("ss_store_s_state", null);
			final SearchRequest searchRequestSubSelect1 = new SearchRequest(indexname);
			searchRequestSubSelect1.source(sourceBuilderSubQuery1);
			searchRequestSubSelect1.types(Constants.TPCDS_DOC_NAME);


			final StopWatch sw = new StopWatch("Query 70", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			sw.beforeExecution();
			// Ausführen Subquery 1
			final SearchResponse searchResponse1 = this.client.search(searchRequestSubSelect1);
			final Aggregations aggregationsSubQuery = searchResponse1.getAggregations();
			final Terms byStateSubAggregation = aggregationsSubQuery.get("s_state");
			for (final Bucket b : byStateSubAggregation.getBuckets()) {
				s_stateArray.add(b.getKeyAsString());
			}


			// Baue Hauptquery und setze Ergebnisse der Subqueries in das Hauptquery
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE

			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());
			final BoolQueryBuilder inQuery = QueryBuilders.boolQuery();
			for (final String inParam : s_stateArray) {
				inQuery.should(QueryBuilders.termQuery("ss_store_s_state.keyword", inParam));
			}

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(QueryBuilders.rangeQuery("ss_sold_date_d_month_seq").from(1212).to(1223))
	                .must(inQuery)
	        );

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("ss_store_s_state")
			        .field("ss_store_s_state.keyword");//.order(Order.aggregation("i_brand>i_class>i_category>qoh", "avg", true));
			aggregation.subAggregation(AggregationBuilders.terms("ss_store_s_county")
					        .field("ss_store_s_county.keyword")
							.subAggregation(AggregationBuilders.sum("total_sum")
					        		.field("ss_ss_net_profit"))
							.subAggregation(AggregationBuilders.topHits("additionalValues")
									.fetchSource(new String[] {"ss_store_s_state", "ss_store_s_county"}, new String[0])
						));

			sourceBuilder.aggregation(aggregation);

			// LIMIT
			sourceBuilder.from(0);
			sourceBuilder.size(100);

			// Ausführen Hauptquery
			final SearchRequest searchRequest = new SearchRequest(indexname);
			searchRequest.source(sourceBuilder);
			searchRequest.types(Constants.TPCDS_DOC_NAME);

			final SearchResponse searchResponse = this.client.search(searchRequest);
			sw.afterExecution();
			//sw.setTookInMillis(searchResponse.getTookInMillis());
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
				final Terms byProductNameAggregation = aggregations.get("ss_store_s_state");
				for (final Bucket b : byProductNameAggregation.getBuckets()) {
					final Terms byBrandAggregation = b.getAggregations().get("ss_store_s_county");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final StringBuilder builder = new StringBuilder();
						final Sum sum = b1.getAggregations().get("total_sum");
						final TopHits topHits = b1.getAggregations().get("additionalValues");
					    for (final SearchHit hit : topHits.getHits().getHits()) {
					        builder.append(hit.getSourceAsString());
					    }
						LOG.debug("Name: {}  \n  -  SUM: {} \n  -  {} ", b1.getKeyAsString(), sum.getValue(), builder.toString());
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
