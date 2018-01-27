package de.funde.elastic.connector.tpcds.queries.messungen;

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
 * select  s_store_name, s_store_id,
        sum(case when (d_day_name='Sunday') then ss_sales_price else null end) sun_sales,
        sum(case when (d_day_name='Monday') then ss_sales_price else null end) mon_sales,
        sum(case when (d_day_name='Tuesday') then ss_sales_price else  null end) tue_sales,
        sum(case when (d_day_name='Wednesday') then ss_sales_price else null end) wed_sales,
        sum(case when (d_day_name='Thursday') then ss_sales_price else null end) thu_sales,
        sum(case when (d_day_name='Friday') then ss_sales_price else null end) fri_sales,
        sum(case when (d_day_name='Saturday') then ss_sales_price else null end) sat_sales
 from date_dim, store_sales, store
 where d_date_sk = ss_sold_date_sk and
       s_store_sk = ss_store_sk and
       s_gmt_offset = -5 and
       d_year = 1998
 group by s_store_name, s_store_id
 order by s_store_name, s_store_id,sun_sales,mon_sales,tue_sales,wed_sales,thu_sales,fri_sales,sat_sales
 limit 100;
 * @author Marco
 *
 */
public class Query43 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query43.class);


	public Query43(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {
			final StopWatch sw = new StopWatch("Query 43", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_date_sk'].value ==  doc['ss_ss_sold_date_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
	                .must(new ScriptQueryBuilder(script))
	                .must(new ScriptQueryBuilder(script1))
	                .must(QueryBuilders.termQuery("ss_store_s_gmt_offset", -5))
	                .must(QueryBuilders.termQuery("ss_sold_date_d_year", 1998))
	        );

			// SORT
			final List<SortBuilder<?>> sortBuilders = new ArrayList<>();
			sortBuilders.add(new FieldSortBuilder("ss_store_s_store_name.keyword").order(SortOrder.ASC));
			sortBuilders.add(new FieldSortBuilder("ss_store_s_store_id.keyword").order(SortOrder.ASC));

			// AGGREGATION AND GROUPING
			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("ss_store_s_store_name")
			        .field("ss_store_s_store_name.keyword");//.order(Order.aggregation("i_brand>i_class>i_category>qoh", "avg", true));
			aggregation.subAggregation(AggregationBuilders.terms("ss_store_s_store_id")
					        .field("ss_store_s_store_id.keyword")
		        			.order(Order.compound(
		        						Order.aggregation("sun_sales", true),
		        						Order.aggregation("mon_sales", true),
		        						Order.aggregation("tue_sales", true),
		        						Order.aggregation("wed_sales", true),
		        						Order.aggregation("thu_sales", true),
		        						Order.aggregation("fri_sales", true),
		        						Order.aggregation("sat_sales", true)))
							.subAggregation(AggregationBuilders.sum("sun_sales")
						        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Sunday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.sum("mon_sales")
							        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Monday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.sum("tue_sales")
							        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Tuesday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.sum("wed_sales")
							        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Wednesday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.sum("thu_sales")
							        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Thursday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.sum("fri_sales")
							        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Friday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.sum("sat_sales")
							        .script(new Script(ScriptType.INLINE, "painless", "doc['ss_sold_date_d_day_name.keyword'].value == 'Saturday' ? doc['ss_ss_sales_price'].value : 0", new HashMap<>())))
							.subAggregation(AggregationBuilders.topHits("additionalValues")
									.fetchSource(new String[] {"ss_store_s_store_name", "ss_store_s_store_id"}, new String[0])
									.sorts(sortBuilders))
					);

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
				final Terms byProductNameAggregation = aggregations.get("ss_store_s_store_name");
				for (final Bucket b : byProductNameAggregation.getBuckets()) {
					final Terms byBrandAggregation = b.getAggregations().get("ss_store_s_store_id");
					for (final Bucket b1 : byBrandAggregation.getBuckets()) {
						final StringBuilder builder = new StringBuilder();
						final Sum sunSales = b1.getAggregations().get("sun_sales");
						final Sum monSales = b1.getAggregations().get("mon_sales");
						final Sum tueSales = b1.getAggregations().get("tue_sales");
						final Sum wedSales = b1.getAggregations().get("wed_sales");
						final Sum thuSales = b1.getAggregations().get("thu_sales");
						final Sum friSales = b1.getAggregations().get("fri_sales");
						final Sum satSales = b1.getAggregations().get("sat_sales");
						final TopHits topHits = b1.getAggregations().get("additionalValues");
					    for (final SearchHit hit : topHits.getHits().getHits()) {
					        builder.append(hit.getSourceAsString());
					    }
						LOG.debug("Name: {}  \n  -  SUMSUNDAY: {} \n  -  SUMMONDAY: {} \n  -  SUMTUESDAY: {} \n  -  SUMWEDNESDAY: {} \n  -  SUMTHURSDAY: {} \n  -  SUMFRIDAY: {} \n  -  SUMSATURDAY: {} \n  -  {} ",
								b1.getKeyAsString(), sunSales.getValue(), monSales.getValue(), tueSales.getValue(), wedSales.getValue(), thuSales.getValue(), friSales.getValue(), satSales.getValue(), builder.toString());

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