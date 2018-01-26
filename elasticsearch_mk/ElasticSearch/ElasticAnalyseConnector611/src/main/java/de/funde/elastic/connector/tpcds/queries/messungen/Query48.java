package de.funde.elastic.connector.tpcds.queries.messungen;

import java.util.HashMap;
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
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.connector.tpcds.queries.IQuery;
import de.funde.elastic.main.ElasticAnalyseConnector;

/**
 * select sum (ss_quantity)
 from store_sales, store, customer_demographics, customer_address, date_dim
 where s_store_sk = ss_store_sk
 and  ss_sold_date_sk = d_date_sk and d_year = 1998
 and
 (
  (
   cd_demo_sk = ss_cdemo_sk
   and
   cd_marital_status = 'M'
   and
   cd_education_status = '4 yr Degree'
   and
   ss_sales_price between 100.00 and 150.00
   )
 or
  (
  cd_demo_sk = ss_cdemo_sk
   and
   cd_marital_status = 'D'
   and
   cd_education_status = 'Primary'
   and
   ss_sales_price between 50.00 and 100.00
  )
 or
 (
  cd_demo_sk = ss_cdemo_sk
  and
   cd_marital_status = 'U'
   and
   cd_education_status = 'Advanced Degree'
   and
   ss_sales_price between 150.00 and 200.00
 )
 )
 and
 (
  (
  ss_addr_sk = ca_address_sk
  and
  ca_country = 'United States'
  and
  ca_state in ('KY', 'GA', 'NM')
  and ss_net_profit between 0 and 2000
  )
 or
  (ss_addr_sk = ca_address_sk
  and
  ca_country = 'United States'
  and
  ca_state in ('MT', 'OR', 'IN')
  and ss_net_profit between 150 and 3000
  )
 or
  (ss_addr_sk = ca_address_sk
  and
  ca_country = 'United States'
  and
  ca_state in ('WI', 'MO', 'WV')
  and ss_net_profit between 50 and 25000
  )
 )
;
 * @author Marco
 *
 */
public class Query48 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query48.class);


	public Query48(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {


			final StopWatch sw = new StopWatch("Query 48", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_year", 1998))

	                .must(QueryBuilders.boolQuery()
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_cdemo_cd_demo_sk'].value ==  doc['ss_ss_cdemo_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "M"))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "4 yr Degree"))
	                				.must(new RangeQueryBuilder("ss_ss_sales_price").from(100.00).to(150.00))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_cdemo_cd_demo_sk'].value ==  doc['ss_ss_cdemo_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "D"))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "Primary"))
	                				.must(new RangeQueryBuilder("ss_ss_sales_price").from(50.00).to(100.00))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_cdemo_cd_demo_sk'].value ==  doc['ss_ss_cdemo_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "U"))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "Advanced Degree"))
	                				.must(new RangeQueryBuilder("ss_ss_sales_price").from(150.00).to(200.00))
	                		)
	                )

	                .must(QueryBuilders.boolQuery()
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_addr_sk'].value ==  doc['ss_addr_ca_address_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_addr_ca_country.keyword", "United States"))
	                				.must(QueryBuilders.boolQuery()
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "KY"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "GA"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "NM"))
	                				)
	                				.must(new RangeQueryBuilder("ss_ss_net_profit").from(0).to(2000))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_addr_sk'].value ==  doc['ss_addr_ca_address_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_addr_ca_country.keyword", "United States"))
	                				.must(QueryBuilders.boolQuery()
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MT"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "OR"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "IN"))
	                				)
	                				.must(new RangeQueryBuilder("ss_ss_net_profit").from(150).to(3000))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_addr_sk'].value ==  doc['ss_addr_ca_address_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_addr_ca_country.keyword", "United States"))
	                				.must(QueryBuilders.boolQuery()
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "WI"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MO"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MV"))
	                				)
	                				.must(new RangeQueryBuilder("ss_ss_net_profit").from(50).to(25000))
	                		)
	                )
	        );



			// AGGREGATION AND GROUPING
			final SumAggregationBuilder aggregation = AggregationBuilders.sum("ss_quantity")
			        .field("ss_ss_quantity");

			sourceBuilder.aggregation(aggregation);


			final SearchRequest searchRequest = new SearchRequest(indexname);
			searchRequest.source(sourceBuilder);
			searchRequest.types(Constants.TPCDS_DOC_NAME);
			sw.beforeExecution();
			final SearchResponse searchResponse = this.client.search(searchRequest);
			searchResponse.getTotalShards();
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
				final Sum sum = aggregations.get("ss_quantity");
				LOG.debug("Name: {}  \n  -  SUM: {} \n", sum.getName(), sum.getValue());

			}
			sw.afterResultPrinting();
			return Optional.of(sw);


		} catch (final Exception e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
		return Optional.empty();
	}

}
