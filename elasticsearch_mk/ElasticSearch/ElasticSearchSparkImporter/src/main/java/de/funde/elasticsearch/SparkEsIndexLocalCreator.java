package de.funde.elasticsearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;



/**
 * Klasse in der probiert werden soll, einen ES Index local zu erstellen und diesen mit Spark nach ES zu schieben
 *
 * {@see http://events.linuxfoundation.org/sites/events/files/slides/using-apache-spark-for-generating-elasticsearch-indices-offline_0.pdf}
 * @author Marco
 *
 */
public class SparkEsIndexLocalCreator {

	public static void sparkLocalIndexCreator() throws IOException, NodeValidationException, InterruptedException, ExecutionException {

		final Settings esSettings = Settings.builder()
									.put("http.enabled", false) // http.enabled = false -> local Node
									.put("transport.type", "local")
									.put("processors", 1)
									.put("index.merge.scheduler.max_thread_count", 1)
									.build();


		// https://www.elastic.co/guide/en/elasticsearch/reference/5.5/breaking_50_java_api_changes.html#_nodebuilder_removed NodeBuilder wurde gelöscht
		// https://www.elastic.co/guide/en/elasticsearch/reference/5.5/breaking_50_settings_changes.html#_node_settings
		// https://github.com/andybab/OfflineESIndexGenerator/tree/master/src/main/scala/sk/eset/dbsystems

		final Map<String, Object> mappingSource = new HashMap<>();
		mappingSource.put("col1", new HashMap<String, String>() {{this.put("type", "text");}});
		mappingSource.put("col2", new HashMap<String, String>() {{this.put("type", "text");}});
		final Node esNode = new Node(esSettings);

		esNode.start();
		final Client client = esNode.client();
		final PutMappingRequest mappingSettings = new PutMappingRequest("indexnameMK");
		mappingSettings.type("typeMK");
		mappingSettings.source(mappingSource);
		final PutMappingResponse mappingResponse = client.admin().indices().putMapping(mappingSettings).get();







		esNode.close();

	}
}
