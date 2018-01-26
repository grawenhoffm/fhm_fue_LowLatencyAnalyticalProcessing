package de.funde.elastic.connector.starjoin;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.connector.IndexQueries;

public class StarJoinIndexQueries implements IndexQueries {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(StarJoinIndexQueries.class);


	public StarJoinIndexQueries(RestHighLevelClient client) {
		this.client = client;
	}


	// Beispiel-Abfrage um an die verschiedenen M�glichkeiten der Resultsets und Attribute zu gelangen
	@Override
	public void getRequest() throws IOException {

		try {
		    final GetRequest request = new GetRequest(StarJoinIndexCreator.INDEX_NAME, "doc", "1");
		    final GetResponse getResponse = this.client.get(request);

		    final String index = getResponse.getIndex();
		    final String type = getResponse.getType();
		    final String id = getResponse.getId();
		    if (getResponse.isExists()) {
		        final long version = getResponse.getVersion();
		        final String sourceAsString = getResponse.getSourceAsString(); // Antwort als String
		        LOG.debug("Version: {} \n {}" , version, sourceAsString);
		        final Map<String, Object> sourceAsMap = getResponse.getSourceAsMap(); // Antwort als Map
		        final byte[] sourceAsBytes = getResponse.getSourceAsBytes(); // Antwort als Byte-Array
		    } else {
		    	LOG.debug("Fehler bei der Abfrage, keine Ergebnismenge.");
		    }

		} catch (final ElasticsearchException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
	}

	/**
	 * Serachrequest mit Sorting
	 */
	@Override
	public void searchRequestWithSort() {

		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//				sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
//				sourceBuilder.from(0);
//				sourceBuilder.size(5);
//				sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


			//sourceBuilder.sort(new FieldSortBuilder("_uid").order(SortOrder.ASC));
			sourceBuilder.sort(new FieldSortBuilder("answer_value").order(SortOrder.DESC));
			final String[] includeFields = new String[] {"answer_value"};
			final String[] excludeFields = new String[0];// {"_type", "text"};
			sourceBuilder.fetchSource(includeFields, excludeFields);
			final SearchRequest searchRequest = new SearchRequest(StarJoinIndexCreator.INDEX_NAME);
			searchRequest.source(sourceBuilder);
			searchRequest.types("doc");
			final SearchResponse searchResponse = this.client.search(searchRequest);
			final StringBuffer sb = new StringBuffer();
			final StringBuffer sb1 = new StringBuffer();
			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				sb.append("----------------------\n");
				sb.append("Id: ").append(hit.getId()).append("\n");
				sb.append(hit.getSourceAsString()).append("\n");
				sb1.append(hit.getSource().get("answer_value")).append(" , ");

			}
			LOG.debug("Search Result: {}",  sb.toString());
			LOG.debug("Nummern sortiert: {}", sb1.toString());

		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
	}

	/**
	 * Serachrequest mit Filter
	 */
	@Override
	public void searchRequestWithParentFilterText() {

		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query( new MatchQueryBuilder("question_id", "4"));

			final SearchRequest searchRequest = new SearchRequest(StarJoinIndexCreator.INDEX_NAME);
			searchRequest.source(sourceBuilder);
			searchRequest.types("doc");
			final SearchResponse searchResponse = this.client.search(searchRequest);
			final StringBuffer sb = new StringBuffer();
			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				sb.append("----------------------\n");
				sb.append("Id: ").append(hit.getId()).append("\n");
				sb.append(hit.getSourceAsString()).append("\n");

			}
			LOG.debug("Search Result: {}",  sb.toString());

		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
	}

	/**
	 * Serachrequest mit Filter
	 */
	@Override
	public void searchRequestWithParentIDFilter() {

		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(QueryBuilders.termQuery("question_id", 4));

			final SearchRequest searchRequest = new SearchRequest(StarJoinIndexCreator.INDEX_NAME);
			searchRequest.source(sourceBuilder);
			searchRequest.types("doc");
			final SearchResponse searchResponse = this.client.search(searchRequest);
			final StringBuffer sb = new StringBuffer();
			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				sb.append("----------------------\n");
				sb.append("Id: ").append(hit.getId()).append("\n");
				sb.append(hit.getSourceAsString()).append("\n");

			}
			LOG.debug("Search Result: {}",  sb.toString());

		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
	}



	/**
	 * Serachrequest mit Filter auf allen Antworten und Summe und AVG �ber alle Values
	 */
	@Override
	public void searchRequestWithParentIDFilterAndAggregation(String parentQuestionId) {

		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(QueryBuilders.termQuery("question_id", parentQuestionId));


			final SumAggregationBuilder aggregationSum = AggregationBuilders.sum("sum_value")
			        .field("answer_value");
			final AvgAggregationBuilder aggregationAvg = AggregationBuilders.avg("avg_value")
			        .field("answer_value");
			sourceBuilder.aggregation(aggregationSum);
			sourceBuilder.aggregation(aggregationAvg);

			final SearchRequest searchRequest = new SearchRequest(StarJoinIndexCreator.INDEX_NAME);
			searchRequest.source(sourceBuilder);
			searchRequest.types("doc");
			final SearchResponse searchResponse = this.client.search(searchRequest);
			final StringBuffer sb = new StringBuffer();

			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				sb.append("----------------------\n");
				sb.append("Id: ").append(hit.getId()).append("\n");
				sb.append(hit.getSourceAsString()).append("\n");

			}
			// Aggregations Ergebnis abfragen
			final Aggregations aggregations = searchResponse.getAggregations();
			final Sum sumSomeValue = aggregations.get("sum_value");
			final Avg avgSomeValue = aggregations.get("avg_value");

			LOG.debug("Antworten für Frage {}:\n{}",  parentQuestionId, sb.toString());
			LOG.debug("Sum-Aggregation Result: {}", sumSomeValue.getValue());
			LOG.debug("Avg-Aggregation Result: {}", avgSomeValue.getValue());
		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
	}

	/**
	 * Serachrequest mit Filter auf allen Antworten und Summe und AVG �ber alle Values nach dem Text groupiert.
	 */
	@Override
	public void searchRequestWithParentIDFilterAndAggregationAndGrouping(String parentQuestionId) {

		try {
			final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(QueryBuilders.termQuery("question_id", parentQuestionId));

			final TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_text")
			        .field("answer_text");
			aggregation.subAggregation(AggregationBuilders.sum("sum_value")
			        .field("answer_value"));
			aggregation.subAggregation(AggregationBuilders.avg("avg_value")
			        .field("answer_value"));
			sourceBuilder.aggregation(aggregation);



			final SearchRequest searchRequest = new SearchRequest(StarJoinIndexCreator.INDEX_NAME);
			searchRequest.source(sourceBuilder);
			searchRequest.types("doc");
			//sourceBuilder.fetchSource(false); // Ignore Souce. Aggregationen werden geladen, aber Hits sind alle leer, au�er Metadaten
			final SearchResponse searchResponse = this.client.search(searchRequest);
			final StringBuffer sb = new StringBuffer();

			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				sb.append("----------------------\n");
				sb.append("Id: ").append(hit.getId()).append("\n");
				sb.append(hit.getSourceAsString()).append("\n");

			}
			LOG.debug("Antworten für Frage {}:\n{}",  parentQuestionId, sb.toString());


			// Aggregations Ergebnis abfragen
			final Aggregations aggregations = searchResponse.getAggregations();
			final Terms byTextAggregation = aggregations.get("by_text");


			for (final Bucket b : byTextAggregation.getBuckets()) {
				final Avg average = b.getAggregations().get("avg_value");
				final Sum sum = b.getAggregations().get("sum_value");
				LOG.debug("Name: {}  -  Sum: {}  -  AVG: {}", b.getKeyAsString(), sum.getValue(), average.getValue());
			}

		} catch (final IOException e) {
			LOG.error("Fehler beim Abruf eines Elasticsearch Eintrages: ", e);
		}
	}
}
