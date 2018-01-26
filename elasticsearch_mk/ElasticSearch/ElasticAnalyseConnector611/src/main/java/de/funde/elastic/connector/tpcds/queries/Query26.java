package de.funde.elastic.connector.tpcds.queries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.index.query.SpanOrQueryBuilder;
import org.elasticsearch.index.query.SpanTermQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.main.ElasticAnalyseConnector;
/**
 * select  i_item_id,
        avg(cs_quantity) agg1,
        avg(cs_list_price) agg2,
        avg(cs_coupon_amt) agg3,
        avg(cs_sales_price) agg4
 from catalog_sales, customer_demographics, date_dim, item, promotion
 where cs_sold_date_sk = d_date_sk and
       cs_item_sk = i_item_sk and
       cs_bill_cdemo_sk = cd_demo_sk and
       cs_promo_sk = p_promo_sk and
       cd_gender = 'F' and
       cd_marital_status = 'W' and
       cd_education_status = 'Primary' and
       (p_channel_email = 'N' or p_channel_event = 'N') and
       d_year = 1998
 group by i_item_id
 order by i_item_id
 limit 100;
 * @author Marco
 *
 */
public class Query26 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query26.class);


	public Query26(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {


		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE

			final Script script = new Script(ScriptType.INLINE, "painless", "doc['cs_sold_date_sk'].value ==  doc['d_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['cs_item_sk'].value ==  doc['i_item_sk'].value", new HashMap<>());
			final Script script2 = new Script(ScriptType.INLINE, "painless", "doc['cs_bill_cdemo_sk'].value ==  doc['cd_demo_sk'].value", new HashMap<>());
			final Script script3 = new Script(ScriptType.INLINE, "painless", "doc['cs_promo_sk'].value ==  doc['p_promo_sk'].value", new HashMap<>());
			final Script script4 = new Script(ScriptType.INLINE, "painless", "doc['inv_item_sk'].value ==  doc['i_item_sk'].value", new HashMap<>());

			final SpanOrQueryBuilder orClause = QueryBuilders.spanOrQuery(new SpanTermQueryBuilder("p_channel_email", "N"));
			orClause.addClause(new SpanTermQueryBuilder("p_channel_event", "N"));


			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(new ScriptQueryBuilder(script2))
	                .must( new ScriptQueryBuilder(script3))
	                .must(new ScriptQueryBuilder(script4))

	                .must(orClause)
	                .must(QueryBuilders.termQuery("d_year", 1998))
	                .must(QueryBuilders.termQuery("cd_education_status", "Primary"))
	                .must(QueryBuilders.termQuery("cd_marital_status", "W"))
	                .must(QueryBuilders.termQuery("cd_gender", "F"))
	        );

			// SORT

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("i_item_id")
			        .field("i_item_id.keyword");
			aggregation.subAggregation(AggregationBuilders.avg("agg1")
					.field("cs_quantity"));
			aggregation.subAggregation(AggregationBuilders.avg("agg2")
			        .field("cs_list_price"));
			aggregation.subAggregation(AggregationBuilders.avg("agg3")
			        .field("cs_coupon_amt"));
			aggregation.subAggregation(AggregationBuilders.avg("agg4")
			        .field("cs_sales_price"));


			sourceBuilder.aggregation(aggregation);

			// LIMIT
			sourceBuilder.from(0);
			sourceBuilder.size(100);

			final SearchRequest searchRequest = new SearchRequest("tpcdsmk"); //new SearchRequest(Constants.TPCDS_INDEX_NAME);
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
				final Terms byItemIdAggregation = aggregations.get("i_item_id");
				for (final Bucket b : byItemIdAggregation.getBuckets()) {

					final Avg average1 = b.getAggregations().get("agg1");
					final Avg average2 = b.getAggregations().get("agg2");
					final Avg average3 = b.getAggregations().get("agg3");
					final Avg average4 = b.getAggregations().get("agg4");

					LOG.debug("Name: {}:  -  AVG1: {}  -  AVG2: {}  -  AVG3: {}  -  AVG4: {}\n ", b.getKeyAsString(), average1.getValue(), average2.getValue(), average3.getValue(), average4.getValue());

				}
			}

		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}

		return Optional.empty();
	}
}
