package de.funde.elastic.connector.join;

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
public class JoinIndexCreator implements IndexCreator {

	private static final Logger LOG = LoggerFactory.getLogger(JoinIndexCreator.class);
	public static final String INDEX_NAME = "fundetestjoinindex";


	private final RestHighLevelClient client;
	private final RestClient restClient;

	/**
	 * Konstruktur
	 * @param client - HighlevelClient
	 * @param restClient - LowLevelClient
	 */
	public JoinIndexCreator(RestHighLevelClient client, RestClient restClient) {
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
		        .put("mapping.single_type", true)
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
		                        .startObject("my_join_field")
		                            .field("type", "join")
		                            .startObject("relations")
			                            .field("user", "question")
			                            .field("question", "answer")
		                            .endObject()
		                        .endObject()
		                    .endObject()
		                .endObject()
		            .endObject()
		        .endObject().string();

		final HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);

		final Response response = this.restClient.performRequest("PUT", INDEX_NAME, new HashMap<>(), entity);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			LOG.debug("Index-Mapping nicht angelegt. Status-Code: {}", response.getStatusLine().getStatusCode());
		}

		// Lege Benutzerdaten an
		this.generateUser("1", "user1", 22);
		this.generateUser("2", "user2", 24);
		// Lege Fragedaten an
		this.generateQuestion("3", "question1", 224, "1");
		this.generateQuestion("4", "question2", 234, "2");
		// Lege Antwortdaten an
		this.generateAnswer("5", "Antowrt A", 243, "4");
		this.generateAnswer("6", "Antowrt A", 223, "4");
		this.generateAnswer("7", "Antowrt C", 213, "3");
		this.generateAnswer("8", "Antowrt B", 253, "4");
		this.generateAnswer("9", "Antowrt A", 263, "4");
		this.generateAnswer("10", "Antowrt B", 273, "4");
	}

	/**
	 * Löscht alle Daten aus dem Index und löscht daraufhin den gesamten Index
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

	/**
	 * Befüllt den Index mit einem User-Eintrag
	 * @param id - ID des Benutzers
	 * @param text - DummyText
	 * @param someValue - Wert für z.B. Aggregationen
	 * @throws IOException
	 */
	private void generateUser(String id, String text, int someValue) throws IOException {
		final Map<String, Object> userJoinFiledMap = new HashMap<>();
		userJoinFiledMap.put("name", "user");
		final Map<String, Object> jsonMapUser = new HashMap<>();
		jsonMapUser.put("text", text);
		jsonMapUser.put("someValue", someValue);
		jsonMapUser.put("my_join_field", userJoinFiledMap);
		final IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "doc", id)
		        .source(jsonMapUser).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
		final IndexResponse indexResponse = this.client.index(indexRequest);
		LOG.debug(indexResponse.toString());
	}

	/**
	 * Befüllt den Index mit einem Frage-Eintrag
	 * @param id - ID der Frage
	 * @param text - DummyText
	 * @param someValue - Wert für z.B. Aggregationen
	 * @param parentId - ID des zugeordneten Benutzers
	 * @throws IOException
	 */
	private void generateQuestion(String id, String text, int someValue, String parentId) throws IOException {
		final Map<String, Object> questionJoinFiledMap = new HashMap<>();
		questionJoinFiledMap.put("name", "question");
		questionJoinFiledMap.put("parent", parentId);
		final Map<String, Object> jsonMapUser = new HashMap<>();
		jsonMapUser.put("text", text);
		jsonMapUser.put("someValue", someValue);
		jsonMapUser.put("my_join_field", questionJoinFiledMap);
		final IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "doc", id)
		        .source(jsonMapUser).setRefreshPolicy(RefreshPolicy.IMMEDIATE).routing(parentId);
		final IndexResponse indexResponse = this.client.index(indexRequest);
		LOG.debug(indexResponse.toString());
	}

	/**
	 * Befüllt den Index mit einem Antwort-Eintrag
	 * @param id - ID der Antwort
	 * @param text - DummyText
	 * @param someValue - Wert für z.B. Aggregationen
	 * @param parentId - ID des zugeordneten Benutzers
	 * @throws IOException
	 */
	private void generateAnswer(String id, String text, int someValue, String parentId) throws IOException {
		final Map<String, Object> answerJoinFiledMap = new HashMap<>();
		answerJoinFiledMap.put("name", "answer");
		answerJoinFiledMap.put("parent", parentId);
		final Map<String, Object> jsonMapUser = new HashMap<>();
		jsonMapUser.put("text", text);
		jsonMapUser.put("someValue", someValue);
		jsonMapUser.put("my_join_field", answerJoinFiledMap);
		final IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "doc", id)
		        .source(jsonMapUser).setRefreshPolicy(RefreshPolicy.IMMEDIATE).routing(parentId);
		final IndexResponse indexResponse = this.client.index(indexRequest);
		LOG.debug(indexResponse.toString());
	}
}
