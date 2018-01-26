package de.funde.elastic.connector.tpcds.queries.messungen;

import java.util.HashMap;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.Constants;
import de.funde.elastic.connector.tpcds.queries.IQuery;
import de.funde.elastic.main.ElasticAnalyseConnector;

/**
 * select  count(*)
from store_sales
    ,household_demographics
    ,time_dim, store
where ss_sold_time_sk = time_dim.t_time_sk
    and ss_hdemo_sk = household_demographics.hd_demo_sk
    and ss_store_sk = s_store_sk
    and time_dim.t_hour = 8
    and time_dim.t_minute >= 30
    and household_demographics.hd_dep_count = 5
    and store.s_store_name = 'ese'
order by count(*)
limit 100;
 * @author Marco
 *
 */
public class Query96 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query96.class);


	public Query96(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {

		try {

			final StopWatch sw = new StopWatch("Query 96", ElasticAnalyseConnector.elasticVersion, ElasticAnalyseConnector.luceneVersion, ElasticAnalyseConnector.numberOfReplicas, ElasticAnalyseConnector.numberOfIndexShards, indexname, counter);
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			// WHERE
			final Script script = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_sold_time_sk'].value ==  doc['ss_sold_time_t_time_sk'].value", new HashMap<>());
			final Script script1 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_hdemo_sk'].value ==  doc['ss_hdemo_hd_demo_sk'].value", new HashMap<>());
			final Script script2 = new Script(ScriptType.INLINE, "painless", "doc['ss_ss_store_sk'].value ==  doc['ss_store_s_store_sk'].value", new HashMap<>());

			sourceBuilder.query(QueryBuilders.boolQuery()
//	                .must(new ScriptQueryBuilder(script))
//	                .must(new ScriptQueryBuilder(script1))
//	                .must(new ScriptQueryBuilder(script2))

	                .must(new RangeQueryBuilder("ss_sold_time_t_minute").gte(30))
	                .must(QueryBuilders.termQuery("ss_store_s_store_name.keyword", "ese"))
	                .must(QueryBuilders.termQuery("ss_hdemo_hd_dep_count", 5))
	                .must(QueryBuilders.termQuery("ss_sold_time_t_hour", 8))
	        );

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

				// Count ausgeben:
				LOG.debug("Count: {}  \n ", searchResponse.getHits().getTotalHits());
			}
			sw.afterResultPrinting();
			return Optional.of(sw);
		} catch (final Exception e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
		return Optional.empty();
	}

}
