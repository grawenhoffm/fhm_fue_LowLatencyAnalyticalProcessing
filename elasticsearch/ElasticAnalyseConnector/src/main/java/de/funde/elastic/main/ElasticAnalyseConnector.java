package de.funde.elastic.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.Version;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.ElasticConnector;
import de.funde.elastic.config.StopWatch;
import de.funde.elastic.connector.tpcds.queries.IQuery;
import de.funde.elastic.connector.tpcds.queries.messungen.Query03;
import de.funde.elastic.connector.tpcds.queries.messungen.Query06;
import de.funde.elastic.connector.tpcds.queries.messungen.Query07;
import de.funde.elastic.connector.tpcds.queries.messungen.Query13;
import de.funde.elastic.connector.tpcds.queries.messungen.Query19;
import de.funde.elastic.connector.tpcds.queries.messungen.Query27;
import de.funde.elastic.connector.tpcds.queries.messungen.Query42;
import de.funde.elastic.connector.tpcds.queries.messungen.Query43;
import de.funde.elastic.connector.tpcds.queries.messungen.Query48;
import de.funde.elastic.connector.tpcds.queries.messungen.Query52;
import de.funde.elastic.connector.tpcds.queries.messungen.Query55;
import de.funde.elastic.connector.tpcds.queries.messungen.Query70;
import de.funde.elastic.connector.tpcds.queries.messungen.Query96;
import de.funde.elastic.connector.tpcds.queries.messungen.Query98;

public class ElasticAnalyseConnector {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticAnalyseConnector.class);

	private static final Logger MEASURES_LOG = LoggerFactory.getLogger("MEASURE_LOGGER");

	public static final boolean WITH_RESULT_LOGGING = false;

	public static String elasticVersion;
	public static String luceneVersion;
	public static String numberOfReplicas;
	public static String numberOfIndexShards;

	public static void main(String[] args) {

		if (args.length != 1) {
			LOG.error("Der erste Übergabeparameter muss der Indexname sein");
			System.exit(5);
		}
		final String indexname = args[0];
		// Header der CSV Loggen
		logMessMetadaten();

		final ElasticConnector c = new ElasticConnector();
		final RestHighLevelClient client = c.getClient();
		final RestClient restClient = c.getLowLevelRestClient();

		// Elasticmetadaten ausfüllen (NummerOfReplicas, ...)
		fillElasticMetadata(client, indexname);


		try {


			final StringBuilder logStringBuilder = new StringBuilder();
			//
			// Do Actions
			//

			/*

			LOG.debug("---------------- Join Index Queries -----------");
			// Lege Index an und füge Daten hinzu für den Join Index
			final JoinIndexCreator joinCreator = new JoinIndexCreator(client, restClient);
			joinCreator.deleteAndCreateIndex();
			final JoinIndexQueries joinQueries = new JoinIndexQueries(client);
			joinQueries.runAll();

			LOG.debug("---------------- StarJoinData Index Queries -----------");
			final StarJoinIndexCreator starJoinCreator = new StarJoinIndexCreator(client, restClient);
			starJoinCreator.deleteAndCreateIndex();
			final StarJoinIndexQueries starJoinQueries = new StarJoinIndexQueries(client);
			starJoinQueries.runAll();

			LOG.debug("---------------- StarJoinData Index Queries -----------");
			final NestedIndexCreator nestedCreator = new NestedIndexCreator(client, restClient);
			nestedCreator.deleteAndCreateIndex();
			final NestedIndexQueries nestedQueries = new NestedIndexQueries(client);
			nestedQueries.runAll();

			 */

			//final Query22 query = new Query22(client);
			//final Query26 query = new Query26(client);
//			final IQuery[] queries = new 	IQuery[]{new Query22(client), new Query22(client), new Query03(client), new Query03(client), new Query07(client), new Query98(client), new Query19(client), new Query52(client), new Query55(client)};
			final IQuery[] queries = new 	IQuery[]
				{
					new Query03(client),
					new Query06(client),
					new Query07(client),
					new Query13(client),
					new Query19(client),
					new Query27(client),
					new Query42(client),
					new Query43(client),
					new Query48(client),
					new Query52(client),
					new Query55(client),
					new Query70(client),
					new Query96(client),
					new Query98(client),
				};

			List<StopWatch> queryTimes;
			for (final IQuery q : queries) {
				queryTimes = new ArrayList<>();
				for (int i = 0; i < 5; i++) {
					final Optional<StopWatch> time = q.run(indexname, i);
					if (time.isPresent()) {
						LOG.error(time.get().getInfo());
						if (i != 0) {
							queryTimes.add(time.get());
						}
						logStringBuilder.append(time.get().getMeasureInfo());
					} else {
						LOG.error("Keine Messung für Query {}", q.getClass());
					}
				}
				final double avg = queryTimes.stream().mapToDouble(x -> x.getTimeForAVG()).average().getAsDouble();
				logStringBuilder.append("\n\"").append(queryTimes.get(0).getQueryName()).append("\";").append(avg);
				logStringBuilder.append("\n\n\n");

			}
//			final IQuery query = new Query03(client);
//			final long start = System.currentTimeMillis();
//			query.run();
//			LOG.error("Query Zeit: {} Millisekunden", ((System.currentTimeMillis() - start)));

			MEASURES_LOG.debug(logStringBuilder.toString());
			// Close HTTPRequest
			c.close();
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static void fillElasticMetadata(RestHighLevelClient client, String indexname) {
		// Get elastic-Metadaten
	    final String sURL = "http://" + ElasticConnector.MAIN_URL + ":" + ElasticConnector.MAIN_PORT + "/" + indexname + "/_settings" + "";

		try {
			final URL url = new URL(sURL);
			final HttpURLConnection request = (HttpURLConnection) url.openConnection();
		    request.connect();
		    final InputStreamReader in = new InputStreamReader((InputStream) request.getContent());
		    final BufferedReader buff = new BufferedReader(in);
		    final StringBuilder jsonContent = new StringBuilder();
		    String line;
		    do {
		      line = buff.readLine();
		      jsonContent.append(line + "\n");
		    } while (line != null);

		    final JSONObject json = new JSONObject(jsonContent.toString());

		    numberOfIndexShards = json.getJSONObject(indexname).getJSONObject("settings").getJSONObject("index").getString("number_of_shards");
		    numberOfReplicas = json.getJSONObject(indexname).getJSONObject("settings").getJSONObject("index").getString("number_of_replicas");
		    in.close();
		    buff.close();
		} catch (final IOException e) {
			LOG.error("Fehler beim Lesen der Index-Settings", e);
		}

		try {
			final MainResponse response = client.info();
			final Version v = response.getVersion();
			elasticVersion = v.toString();
			luceneVersion = v.luceneVersion.toString();
		} catch (final IOException e) {
			LOG.error("Fehler beim Lesen der ES-Metadaten.", e);
		}

	}

	private static void logMessMetadaten() {
		final StringBuilder logMetadata = new StringBuilder();
		logMetadata.append("\"").append("Durchlauf").append("\";"); // Durchlauf
		logMetadata.append("\"").append("LuceneVersion").append("\";"); // LuceneVersion
		logMetadata.append("\"").append("ElasticVersion").append("\";"); // ElasticVersion
		logMetadata.append("\"").append("Indexname").append("\";"); // Indexname
		logMetadata.append("\"ReplicaAnzahl").append("\";"); // ReplicaAnzahl
		logMetadata.append("\"Gesamtanzahl Shards des Index").append("\";"); // Shardsbenutzt Anzahl
		logMetadata.append("\"Gesamtanzahl Shards").append("\";"); // Shardsbenutzt Anzahl
		logMetadata.append("\"Erfolgreiche Shards").append("\";"); // Erfolgreiche Shards
		logMetadata.append("\"Fehlgeschlagene Shards").append("\";"); // Fehlgeschlagene Shards
		logMetadata.append("\"Uebersprungene Shards").append("\";"); // Übersprungene Shards
		logMetadata.append("\"Anzahl der ReducePhasen").append("\";"); // Anzahl der ReducePhasen
		logMetadata.append("\"Queryname").append("\";"); // Queryname
		logMetadata.append("\"BuildRequest (ms)").append("\";"); // BuildRequest
		logMetadata.append("\"Zeit nach der Response (ms)").append("\";"); // Zeit nach der Response
		logMetadata.append("\"Zeit nach der Ergebnisausgabe (ms)").append("\";"); //  Zeit nach der Ergebnisausgabe
		logMetadata.append("\"Zeit allein für die Ausfuehrung (ms)").append("\";"); // Zeit allein für die Ausführung
		logMetadata.append("\"Zeit durch ES gemessen (ms)").append("\";"); //  Zeit durch ES gemessen
		MEASURES_LOG.debug("\n\n\n\n" + logMetadata.toString());
	}
}
