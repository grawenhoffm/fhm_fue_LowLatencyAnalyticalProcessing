package de.funde.elastic.connector.tpcds.queries.messungen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
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
 * select  i_item_id,
        s_state, grouping(s_state) g_state,
        avg(ss_quantity) agg1,
        avg(ss_list_price) agg2,
        avg(ss_coupon_amt) agg3,
        avg(ss_sales_price) agg4
 from store_sales, customer_demographics, date_dim, store, item
 where ss_sold_date_sk = d_date_sk and
       ss_item_sk = i_item_sk and
       ss_store_sk = s_store_sk and
       ss_cdemo_sk = cd_demo_sk and
       cd_gender = 'F' and
       cd_marital_status = 'W' and
       cd_education_status = 'Primary' and
       d_year = 1998 and
       s_state in ('TN','TN', 'TN', 'TN', 'TN', 'TN')
 group by rollup (i_item_id, s_state)
 order by i_item_id
         ,s_state
 limit 100;

 * @author Marco
 *
 */
public class Query27 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query27.class);


	public Query27(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {

			final StopWatch sw = new StopWatch("Query 27", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_item_sk'].value ==  doc['ss_item_i_item_sk'].value", new HashMap<>());
			final Script script2 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_cdemo_sk'].value ==  doc['ss_cdemo_cd_demo_sk'].value", new HashMap<>());
			final Script script3 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
//	                .must(new ScriptQueryBuilder(script))
//	                .must(new ScriptQueryBuilder(script1))
//	                .must(new ScriptQueryBuilder(script2))
//	                .must(new ScriptQueryBuilder(script3))

	                .must(QueryBuilders.termQuery("ss_cdemo_cd_gender.keyword", "F"))
	                .must(QueryBuilders.termQuery("ss_cdemo_cd_marital_status.keyword", "W"))
	                .must(QueryBuilders.termQuery("ss_cdemo_cd_education_status.keyword", "Primary"))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_year", 1998))


	                .should(QueryBuilders.termQuery("ss_store_s_state.keyword", "TN"))
	                .should(QueryBuilders.termQuery("ss_store_s_state.keyword", "TN"))
	                .should(QueryBuilders.termQuery("ss_store_s_state.keyword", "TN"))
	                .should(QueryBuilders.termQuery("ss_store_s_state.keyword", "TN"))
	                .should(QueryBuilders.termQuery("ss_store_s_state.keyword", "TN"))
	                .should(QueryBuilders.termQuery("ss_store_s_state.keyword", "TN"))
	        );

			// SORT
			final List<SortBuilder<?>> sortBuilders = new ArrayList<>();
			sortBuilders.add(new FieldSortBuilder("ss_item_i_item_id.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_store_s_state.keyword").order(SortOrder.ASC));

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("i_item_id")
			        .field("ss_item_i_item_id.keyword");
			aggregation.subAggregation(AggregationBuilders.terms("s_state")
					        .field("ss_store_s_state.keyword")
					        .subAggregation(AggregationBuilders.avg("agg1")
									.field("ss_ss_quantity"))
							.subAggregation(AggregationBuilders.avg("agg2")
									.field("ss_ss_list_price"))
							.subAggregation(AggregationBuilders.avg("agg3")
									.field("ss_ss_coupon_amt"))
							.subAggregation(AggregationBuilders.avg("agg4")
									.field("ss_ss_sales_price"))
							.subAggregation(AggregationBuilders.topHits("additionalValues")
									.fetchSource(new String[] {"ss_item_i_item_id", "ss_store_s_state"}, new String[0])
									.sorts(sortBuilders)));

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
					final Terms byBrandAggregation = b.getAggregations().get("s_state");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final Avg average1 = b1.getAggregations().get("agg1");
						final Avg average2 = b1.getAggregations().get("agg2");
						final Avg average3 = b1.getAggregations().get("agg3");
						final Avg average4 = b1.getAggregations().get("agg4");
						LOG.debug("Name: {}:  -  AVG1: {}  -  AVG2: {}  -  AVG3: {}  -  AVG4: {}\n ", b1.getKeyAsString(), average1.getValue(), average2.getValue(), average3.getValue(), average4.getValue());
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
