package de.funde.elastic.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Klasse für die Schnittstelle zu Elasticsearch
 * Baut eine Verbindung zum Cluster auf
 * @author Marco
 *
 */
public class ElasticConnector {

	private final RestHighLevelClient client;

	public static final String MAIN_URL = "10.42.13.203";
	public static final int MAIN_PORT = 9205;

	public ElasticConnector() {

		this.client = new RestHighLevelClient(RestClient.builder(
				//new HttpHost("localhost", 9200, "http")).build(); // Local
//				new HttpHost("10.60.64.84", 9200, "http"),// WI-Cluster
//				new HttpHost("10.60.64.83", 9200, "http"),// WI-Cluster
//				new HttpHost("10.60.70.7", 9200, "http")// WI-Cluster
				new HttpHost(MAIN_URL, 9206, "http"),
				new HttpHost(MAIN_URL, 9207, "http"),
				new HttpHost(MAIN_URL, MAIN_PORT, "http") // DVZ-Cluster

		));
	}

	/**
	 * Gebe den HighLevelRestClient zurück
	 * @return ES-HighLevelClient
	 */
	public RestHighLevelClient getClient() {
		return this.client;
	}


	/**
	 * Gebe alle Ressourcen wieder frei
	 * @throws IOException
	 */
	public void close () throws IOException {
		this.client.close();
	}
}
