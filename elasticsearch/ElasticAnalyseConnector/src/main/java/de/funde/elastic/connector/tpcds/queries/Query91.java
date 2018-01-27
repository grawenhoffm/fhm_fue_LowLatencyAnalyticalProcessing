package de.funde.elastic.connector.tpcds.queries;

import java.io.IOException;
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
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.main.ElasticAnalyseConnector;

/**
 * select
        cc_call_center_id Call_Center,
        cc_name Call_Center_Name,
        cc_manager Manager,
        sum(cr_net_loss) Returns_Loss
from
        call_center,
        catalog_returns,
        date_dim,
        customer,
        customer_address,
        customer_demographics,
        household_demographics
where
        cr_call_center_sk       = cc_call_center_sk
and     cr_returned_date_sk     = d_date_sk
and     cr_returning_customer_sk= c_customer_sk
and     cd_demo_sk              = c_current_cdemo_sk
and     hd_demo_sk              = c_current_hdemo_sk
and     ca_address_sk           = c_current_addr_sk
and     d_year                  = 1999
and     d_moy                   = 11
and     ( (cd_marital_status       = 'M' and cd_education_status     = 'Unknown')
        or(cd_marital_status       = 'W' and cd_education_status     = 'Advanced Degree'))
and     hd_buy_potential like '0-500%'
and     ca_gmt_offset           = -7
group by cc_call_center_id,cc_name,cc_manager,cd_marital_status,cd_education_status
order by sum(cr_net_loss) desc;

 * @author Marco
 *
 */
public class Query91 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query91.class);


	public Query91(RestHighLevelClient client) {
		this.client = client;
	}


	@Override
	public Optional<StopWatch> run(String indexname, int counter) {
		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['cr_call_center_sk'].value ==  doc['cc_call_center_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['cr_returned_date_sk'].value ==  doc['d_date_sk'].value", new HashMap<>());
			final Script script2 = new Script(ScriptType.INLINE, "painless", "doc['cr_returning_customer_sk'].value ==  doc['c_customer_sk'].value", new HashMap<>());
			final Script script3 = new Script(ScriptType.INLINE, "painless", "doc['cd_demo_sk'].value ==  doc['c_current_cdemo_sk'].value", new HashMap<>());
			final Script script4 = new Script(ScriptType.INLINE, "painless", "doc['hd_demo_sk'].value ==  doc['c_current_hdemo_sk'].value", new HashMap<>());
			final Script script5 = new Script(ScriptType.INLINE, "painless", "doc['ca_address_sk'] ==  doc['c_current_addr_sk']", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(new ScriptQueryBuilder(script2))
	                .must( new ScriptQueryBuilder(script3))
	                .must(new ScriptQueryBuilder(script4))
	                .must(new ScriptQueryBuilder(script5))

	                .must(QueryBuilders.termQuery("hd_buy_potential", "0-500%"))
					.must(QueryBuilders.termQuery("d_moy", 11))
					.must(QueryBuilders.termQuery("d_year", 1999))
					.must(QueryBuilders.termQuery("ca_gmt_offset", -7))

					.should(QueryBuilders.boolQuery()
							.must(QueryBuilders.termQuery("cd_marital_status", "M"))
							.must(QueryBuilders.termQuery("cd_education_status", "Unknown"))
					)
					.should(QueryBuilders.boolQuery()
							.must(QueryBuilders.termQuery("cd_marital_status", "W"))
							.must(QueryBuilders.termQuery("cd_education_status", "Advanced Degree"))
					)
	        );

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("cc_call_center_id")
			        .field("cc_call_center_id");
			aggregation.subAggregation(AggregationBuilders.terms("cc_name")
					        .field("cc_name.keyword")
				        		.subAggregation(AggregationBuilders.terms("cc_manager")
				        				.field("cc_manager.keyword")
				        				.subAggregation(AggregationBuilders.terms("cd_marital_status")
						        				.field("cd_marital_status.keyword")
						        				.subAggregation(AggregationBuilders.terms("cd_education_status")
								        				.field("cd_education_status.keyword")
								        				.order(Order.aggregation("Returns_Loss", false))
														.subAggregation(AggregationBuilders.sum("Returns_Loss")
												        		.field("cr_net_loss"))
																.subAggregation(AggregationBuilders.topHits("additionalValues")
																		.fetchSource(new String[] {"cc_call_center_id", "cc_name", "cc_manager"}, new String[0])
															)))));

			sourceBuilder.aggregation(aggregation);

			final SearchRequest searchRequest = new SearchRequest("tpcdsmk19");
			searchRequest.source(sourceBuilder);
			searchRequest.types(Constants.TPCDS_DOC_NAME);
			final SearchResponse searchResponse = this.client.search(searchRequest);
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
				final Terms byProductNameAggregation = aggregations.get("cc_call_center_id");
				for (final Bucket b : byProductNameAggregation.getBuckets()) {
					final Terms byBrandAggregation = b.getAggregations().get("cc_name");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final Terms byClassAggregation = b1.getAggregations().get("cc_manager");
						for (final Bucket b2 : byClassAggregation.getBuckets()) {
							final Terms byCategoryAggregation = b2.getAggregations().get("cd_marital_status");
							for (final Bucket b3 : byCategoryAggregation.getBuckets()) {
								final Terms byEduAggregation = b3.getAggregations().get("cd_education_status");
								for (final Bucket b4 : byEduAggregation.getBuckets()) {
									final StringBuilder builder = new StringBuilder();
									final Sum average = b4.getAggregations().get("Returns_Loss");
									final TopHits topHits = b4.getAggregations().get("additionalValues");
								    for (final SearchHit hit : topHits.getHits().getHits()) {
								        builder.append(hit.getSourceAsString());
								    }
									LOG.debug("Name: {}  \n  -  SUM: {} \n  -  {} ", b4.getKeyAsString(), average.getValue(), builder.toString());
								}
							}
						}
					}
				}
			}
		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
		return Optional.empty();
	}

}
