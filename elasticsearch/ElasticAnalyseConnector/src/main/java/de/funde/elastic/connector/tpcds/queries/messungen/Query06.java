package de.funde.elastic.connector.tpcds.queries.messungen;

import java.util.HashMap;
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
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.connector.tpcds.queries.IQuery;
import de.funde.elastic.main.ElasticAnalyseConnector;


/**
 * select  a.ca_state state, count(*) cnt
 from customer_address a
     ,customer c
     ,store_sales s
     ,date_dim d
     ,item i
 where       a.ca_address_sk = c.c_current_addr_sk
 	and c.c_customer_sk = s.ss_customer_sk
 	and s.ss_sold_date_sk = d.d_date_sk
 	and s.ss_item_sk = i.i_item_sk
 	and d.d_month_seq =
 	     (select distinct (d_month_seq)
 	      from date_dim
               where d_year = 2000
 	        and d_moy = 2 )
 	and i.i_current_price > 1.2 *
             (select avg(j.i_current_price)
 	     from item j
 	     where j.i_category = i.i_category)
 group by a.ca_state
 having count(*) >= 10
 order by cnt
 limit 100;
 * @author Marco
 *
 */
public class Query06 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query06.class);


	public Query06(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {
			// SubqueryResults
			long d_month_seq = -1L;
//			double avg_current_price = -1.0;// Raus weil Druid das nicht kann

			//
			// Erstes Subquery:
			//
			final SearchSourceBuilder sourceBuilderSubQuery1 = new SearchSourceBuilder();
			sourceBuilderSubQuery1.query(QueryBuilders.boolQuery()
	                .must(QueryBuilders.termQuery("ss_sold_date_d_year", 2000))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_moy", 2))
	        );
			// Distinct in Limit überführen
			sourceBuilderSubQuery1.from(0);
			sourceBuilderSubQuery1.size(1);

			sourceBuilderSubQuery1.fetchSource("ss_sold_date_d_month_seq", null);
			final SearchRequest searchRequestSubSelect1 = new SearchRequest(indexname);
			searchRequestSubSelect1.source(sourceBuilderSubQuery1);
			searchRequestSubSelect1.types(Constants.TPCDS_DOC_NAME);


			//
			// Zweites Subquery:
			//
//			final SearchSourceBuilder sourceBuilderSubQuery2 = new SearchSourceBuilder();
//			final AvgAggregationBuilder subQueryAvg = AggregationBuilders.avg("subqueryAvg").field("ss_item_i_current_price");
//			sourceBuilderSubQuery2.aggregation(subQueryAvg);
//			final SearchRequest searchRequestSubSelect2 = new SearchRequest(indexname);
//			searchRequestSubSelect2.source(sourceBuilderSubQuery2);
//			searchRequestSubSelect2.types(Constants.TPCDS_DOC_NAME);// Raus weil Druid das nicht kann


			final StopWatch sw = new StopWatch("Query 06", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			sw.beforeExecution();
			// Ausführen Subquery 1
			final SearchResponse searchResponse1 = this.client.search(searchRequestSubSelect1);
			if (searchResponse1.getHits().getHits().length > 0) {
				d_month_seq = Long.parseLong(String.valueOf(searchResponse1.getHits().getHits()[0].getSourceAsMap().get("ss_sold_date_d_month_seq")));
			}

			// Ausführen Subquery 2
//			final SearchResponse searchResponse2 = this.client.search(searchRequestSubSelect2);
//			final Aggregations aggregationsSubquery = searchResponse2.getAggregations();
//			final Avg avgAgg = aggregationsSubquery.get("subqueryAvg");
//			avg_current_price = avgAgg.getValue();// Raus weil Druid das nicht kann

			// Baue Hauptquery und setze Ergebnisse der Subqueries in das Hauptquery
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE

			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_item_sk'].value ==  doc['ss_item_i_item_sk'].value", new HashMap<>());
			final Script script2 = new Script(ScriptType.INLINE, "painless", "doc['ss_addr_ca_address_sk'].value ==  doc['ss_customer_c_current_addr_sk'].value", new HashMap<>());
			final Script script3 = new Script(ScriptType.INLINE, "painless", "doc['ss_customer_c_customer_sk'].value ==  doc['ss_ss_customer_sk'].value", new HashMap<>());
			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(new ScriptQueryBuilder(script2))
	                .must(new ScriptQueryBuilder(script3))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_month_seq", d_month_seq))
//	                .must(QueryBuilders.rangeQuery("ss_item_i_current_price").gt(1.2*avg_current_price))// Raus weil Druid das nicht kann
	        );

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("ss_addr_ca_state")
			        .field("ss_addr_ca_state.keyword");//.order(Order.aggregation("i_brand>i_class>i_category>qoh", "avg", true));
			aggregation.subAggregation(AggregationBuilders.topHits("additionalValues")
								.fetchSource(new String[] {"ss_addr_ca_state"}, new String[0])
						);

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
				final Terms byStateAggregation = aggregations.get("ss_addr_ca_state");
				for (final Bucket b : byStateAggregation.getBuckets()) {
					if (b.getDocCount() >= 10) {
						final StringBuilder builder = new StringBuilder();
						final TopHits topHits = b.getAggregations().get("additionalValues");
					    for (final SearchHit hit : topHits.getHits().getHits()) {
					        builder.append(hit.getSourceAsString());
					    }
						LOG.debug("Name: {}  \n  -  Count: {} \n  -  {} ", b.getKeyAsString(), b.getDocCount(), builder.toString());
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

