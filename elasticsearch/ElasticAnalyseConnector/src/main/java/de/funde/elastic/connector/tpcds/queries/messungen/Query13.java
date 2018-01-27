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
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
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
 *
select avg(ss_quantity)
       ,avg(ss_ext_sales_price)
       ,avg(ss_ext_wholesale_cost)
       ,sum(ss_ext_wholesale_cost)
 from store_sales
     ,store
     ,customer_demographics
     ,household_demographics
     ,customer_address
     ,date_dim
 where s_store_sk = ss_store_sk
 and  ss_sold_date_sk = d_date_sk and d_year = 2001
 and(
  (ss_hdemo_sk=hd_demo_sk
  and cd_demo_sk = ss_cdemo_sk
  and cd_marital_status = 'D'
  and cd_education_status = '2 yr Degree'
  and ss_sales_price between 100.00 and 150.00
  and hd_dep_count = 3
     )or
     (ss_hdemo_sk=hd_demo_sk
  and cd_demo_sk = ss_cdemo_sk
  and cd_marital_status = 'S'
  and cd_education_status = 'Secondary'
  and ss_sales_price between 50.00 and 100.00
  and hd_dep_count = 1
     ) or
     (ss_hdemo_sk=hd_demo_sk
  and cd_demo_sk = ss_cdemo_sk
  and cd_marital_status = 'W'
  and cd_education_status = 'Advanced Degree'
  and ss_sales_price between 150.00 and 200.00
  and hd_dep_count = 1
     )
 )
 and(
  (ss_addr_sk = ca_address_sk
  and ca_country = 'United States'
  and ca_state in ('CO', 'IL', 'MN')
  and ss_net_profit between 100 and 200
     ) or
     (ss_addr_sk = ca_address_sk
  and ca_country = 'United States'
  and ca_state in ('OH', 'MT', 'NM')
  and ss_net_profit between 150 and 300
     ) or
     (ss_addr_sk = ca_address_sk
  and ca_country = 'United States'
  and ca_state in ('TX', 'MO', 'MI')
  and ss_net_profit between 50 and 250
  )
 )
;

 * @author Marco
 *
 */
public class Query13 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query13.class);


	public Query13(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {
			final StopWatch sw = new StopWatch("Query 13", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_year", 2001))
	                .must(QueryBuilders.boolQuery()
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_hdemo_sk'].value ==  doc['ss_hdemo_hd_demo_sk'].value", new HashMap<>())))
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_cdemo_cd_demo_sk'].value ==  doc['ss_ss_cdemo_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "D"))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "2 yr Degree"))
	                				.must(new RangeQueryBuilder("ss_ss_sales_price").from(100.00).to(150.00))
	                				.must(QueryBuilders.termQuery("ss_hdemo_hd_dep_count", 3))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_hdemo_sk'].value ==  doc['ss_hdemo_hd_demo_sk'].value", new HashMap<>())))
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_cdemo_cd_demo_sk'].value ==  doc['ss_ss_cdemo_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "S"))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "Secondary"))
	                				.must(new RangeQueryBuilder("ss_ss_sales_price").from(50.00).to(100.00))
	                				.must(QueryBuilders.termQuery("ss_hdemo_hd_dep_count", 1))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_hdemo_sk'].value ==  doc['ss_hdemo_hd_demo_sk'].value", new HashMap<>())))
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_cdemo_cd_demo_sk'].value ==  doc['ss_ss_cdemo_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "W"))
	                				.must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "Advanced Degree"))
	                				.must(new RangeQueryBuilder("ss_ss_sales_price").from(150.00).to(200.00))
	                				.must(QueryBuilders.termQuery("ss_hdemo_hd_dep_count", 1))
	                		)
	                )
	                .must(QueryBuilders.boolQuery()
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_addr_sk'].value ==  doc['ss_addr_ca_address_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_addr_ca_country.keyword", "United States"))
	                				.must(QueryBuilders.boolQuery()
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "CO"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "IL"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MN"))
	                				)
	                				.must(new RangeQueryBuilder("ss_ss_net_profit").from(100).to(200))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_addr_sk'].value ==  doc['ss_addr_ca_address_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_addr_ca_country.keyword", "United States"))
	                				.must(QueryBuilders.boolQuery()
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "OH"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MT"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "NM"))
	                				)
	                				.must(new RangeQueryBuilder("ss_ss_net_profit").from(150).to(300))
	                		)
	                		.should(QueryBuilders.boolQuery()
	                				.must(new ScriptQueryBuilder(new Script(ScriptType.INLINE, "painless", "doc['ss_ss_addr_sk'].value ==  doc['ss_addr_ca_address_sk'].value", new HashMap<>())))
	                				.must(QueryBuilders.termQuery("ss_addr_ca_country.keyword", "United States"))
	                				.must(QueryBuilders.boolQuery()
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "TX"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MO"))
	                						.should(QueryBuilders.termQuery("ss_addr_ca_state.keyword", "MI"))
	                				)
	                				.must(new RangeQueryBuilder("ss_ss_net_profit").from(50).to(250))
	                		)
	                )
	        );


			// AGGREGATION AND GROUPING
			final AvgAggregationBuilder aggregation = AggregationBuilders.avg("ss_quantity")
			        .field("ss_ss_quantity");
			final AvgAggregationBuilder aggregation1 = AggregationBuilders.avg("ss_ext_sales_price")
			        .field("ss_ss_ext_sales_price");
			final AvgAggregationBuilder aggregation2 = AggregationBuilders.avg("ss_ext_wholesale_cost_avg")
			        .field("ss_ss_ext_wholesale_cost");
			final SumAggregationBuilder aggregation3 = AggregationBuilders.sum("ss_ext_wholesale_cost_sum")
			        .field("ss_ss_ext_wholesale_cost");
			sourceBuilder.aggregation(aggregation).aggregation(aggregation1).aggregation(aggregation2).aggregation(aggregation3);


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
				final Avg agg1 = aggregations.get("ss_quantity");
				final Avg agg2 = aggregations.get("ss_ext_sales_price");
				final Avg agg3 = aggregations.get("ss_ext_wholesale_cost_avg");
				final Sum sum = aggregations.get("ss_ext_wholesale_cost_sum");
				LOG.debug("Name: {}  \n  -  AVG1: {} \n  -  AVG2: {} \n  -  AVG3: {} \n  -  SUM: {} \n", agg1.getName(), agg1.getValue(), agg2.getValue(), agg3.getValue(), sum.getValue());

			}
			sw.afterResultPrinting();
			return Optional.of(sw);

		} catch (final Exception e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
		return Optional.empty();
	}

}
