package de.funde.elastic.connector.starjoin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.connector.IndexCreator;

/**
 * Klasse, die einen Index erzeugt und ihn mit Daten befüllt
 * @author Marco
 *
 */
public class StarJoinIndexCreator implements IndexCreator {

	private static final Logger LOG = LoggerFactory.getLogger(StarJoinIndexCreator.class);
	public static final String INDEX_NAME = "fundeteststarjoinindex";


	private final RestHighLevelClient client;
	private final RestClient restClient;

	/**
	 * Konstruktur
	 * @param client - HighlevelClient
	 * @param restClient - LowLevelClient
	 */
	public StarJoinIndexCreator(RestHighLevelClient client, RestClient restClient) {
		this.client = client;
		this.restClient = restClient;
	}


	/**
	 * Löscht alle Daten aus dem Index und Legt diesen mit dem Mapping neu an
	 * Draaufhin wird dieser mit Test-Daten befüllt
	 * @throws IOException
	 */
	@Override
	public void deleteAndCreateIndex() throws IOException {
		// DELETE INDEX
		this.deleteAll();

		// CREATE INDEX
		final Settings indexSettings = Settings.builder()
				// TODO Settings setzen
		        .build();


		// Legt den Index mit dem Mapping an. Dies wird noch nicht durch den HighLevelClient unterst�tzt. Hierf�r muss der LowLevelClient verwendet werden
		final String payload = XContentFactory.jsonBuilder()
		        .startObject()
		            .startObject("settings")
		                .value(indexSettings)
		            .endObject()
		            .startObject("mappings")
		                .startObject("doc")
		                    .startObject("properties")
				                .startObject("id")
		                            .field("type", "integer")
		                        .endObject()
		                        .startObject("q_user_id")
		                            .field("type", "integer")
		                        .endObject()
			                    .startObject("q_user_name")
		                            .field("type", "text")
		                        .endObject()
		                        .startObject("q_user_value")
                            		.field("type", "integer")
                            	.endObject()
		                        .startObject("a_user_id")
	                            	.field("type", "integer")
		                        .endObject()
		                        .startObject("a_user_name")
	                        		.field("type", "text")
			                    .endObject()
			                    .startObject("a_user_value")
                            		.field("type", "integer")
                            	.endObject()
			                    .startObject("question_id")
			                    	.field("type", "integer")
			                    .endObject()
			                    .startObject("question_text")
			                    	.field("type", "text")
			                    .endObject()
			                    .startObject("question_value")
		                    		.field("type", "integer")
		                    	.endObject()
		                    	.startObject("answer_id")
			                    	.field("type", "integer")
			                    .endObject()
			                    .startObject("answer_text")
			                    	.field("type", "keyword")
			                    .endObject()
			                    .startObject("answer_value")
		                    		.field("type", "integer")
		                    	.endObject()
		                    .endObject()
		                .endObject()
		            .endObject()
		        .endObject().string();

		final HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);

		try {
			final Response response = this.restClient.performRequest("PUT", INDEX_NAME, new HashMap<>(), entity);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				LOG.debug("Index-Mapping nicht angelegt. Status-Code: {}", response.getStatusLine().getStatusCode());
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}

		// Lege Benutzerdaten an
		this.generateEntry(1, 1, "user1", 22, 2, "user2", 24, 3, "question1", 224, 1, "Antowrt A", 243);
		this.generateEntry(2, 1, "user1", 22, 2, "user2", 24, 4, "question2", 234, 2, "Antowrt B", 222);
		this.generateEntry(3, 1, "user1", 22, 2, "user2", 24, 4, "question2", 243, 3, "Antowrt A", 213);
		this.generateEntry(4, 1, "user1", 22, 2, "user2", 24, 3, "question1", 223, 4, "Antowrt B", 321);
		this.generateEntry(5, 1, "user1", 22, 2, "user2", 24, 4, "question2", 253, 5, "Antowrt B", 212);
		this.generateEntry(6, 1, "user1", 22, 2, "user2", 24, 4, "question2", 213, 6, "Antowrt A", 214);
		this.generateEntry(7, 1, "user1", 22, 2, "user2", 24, 3, "question1", 111, 7, "Antowrt B", 222);
		this.generateEntry(8, 1, "user1", 22, 2, "user2", 24, 4, "question2", 263, 8, "Antowrt A", 236);
		this.generateEntry(9, 1, "user1", 22, 2, "user2", 24, 3, "question1", 222, 9, "Antowrt A", 127);
		this.generateEntry(10, 1, "user1", 22, 2, "user2", 24, 3, "question1", 273, 10, "Antowrt A", 234);

	}


	/**
	 * Befüllt den Index mit einem User-Eintrag
	 * @throws IOException
	 */
	private void generateEntry(int id, int q_user_id, String q_user_text, int q_user_someValue,
			int a_user_id, String a_user_text, int a_user_someValue,
			int question_id, String question_text, int question_someValue,
			int answer_id, String answer_text, int answer_someValue) throws IOException {

		final Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("q_user_id", q_user_id);
		jsonMap.put("q_user_name", q_user_text);
		jsonMap.put("q_user_value", q_user_someValue);

		jsonMap.put("a_user_id", a_user_id);
		jsonMap.put("a_user_name", a_user_text);
		jsonMap.put("a_user_value", a_user_someValue);

		jsonMap.put("question_id", question_id);
		jsonMap.put("question_text", question_text);
		jsonMap.put("question_value", question_someValue);

		jsonMap.put("answer_id", answer_id);
		jsonMap.put("answer_text", answer_text);
		jsonMap.put("answer_value", answer_someValue);


		final IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "doc", String.valueOf(id))
		        .source(jsonMap).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
		final IndexResponse indexResponse = this.client.index(indexRequest);
		LOG.debug(indexResponse.toString());
	}

	/**
	 * Löscht alle Daten aus dem Index und l�scht daraufhin den gesamten Index
	 * @throws IOException
	 *
	 */
	private void deleteAll() throws IOException {
		for (int i = 1; i<1; i++) {
			final DeleteRequest request = new DeleteRequest(INDEX_NAME, "doc", String.valueOf(i));
			DeleteResponse deleteResponse;
			try {
				deleteResponse = this.client.delete(request);
				LOG.debug("Index gelöscht. Status: {}", deleteResponse.status());
			} catch (final IOException e) {
				LOG.error("Fehler beim Löschen des Elementes mit der ID: "+ i , e);
			}
		}
		try {
			// Löscht den gasammten Index
			final Response response = this.restClient.performRequest("DELETE", INDEX_NAME);
			LOG.debug("Index gelöscht: {}", response);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
