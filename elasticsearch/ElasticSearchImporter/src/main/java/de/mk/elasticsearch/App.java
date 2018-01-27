package de.mk.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import de.mk.elasticsearch.csvpreparation.CSVPreparation;
import de.mk.elasticsearch.tableobjects.Column;
import de.mk.elasticsearch.tableobjects.Table;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

/**
 * Elastic Search Demo App
 *
 * @author Matt Tyson
 */
public class App {
	protected static ObjectMapper mapper = JsonFactory.create();



	public static void main(String[] args) throws Exception {
		final List<Table> tables = readFile();
		final LogstashImporter importer = new LogstashImporter();
		final CSVPreparation csvprep = new CSVPreparation();
		//csvprep.prepareAll();

		final String f = "hahah|hshjjs||jsjsj||||jskksk";
//		final String  [] arr = f.split("\\|");
		final String x = f.replaceAll("\\|\\|", "|null|");

		for (final Table t : tables) {
//			System.out.println(t.toString());
		}
		importer.generateLogstashImport(tables);
	}

	private static List<Table> readFile() throws FileNotFoundException, IOException {

		final StringBuffer buf = new StringBuffer();
		final List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(App.class.getResource("/tpcds.sql").getPath())))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if (!line.trim().isEmpty() && !"".equals(line.trim())) {
		    		buf.append(line + "\n");
		    		lines.add(line);
		    	}
		    }
		}
		final List<Table> tables = new ArrayList<>();
		final String complettCreateScript = buf.toString();
		final String[] tableCreates = complettCreateScript.split("create table");

		loop : for (final String t : tableCreates) {
			if (t.trim().isEmpty() || "".equals(t.trim()) || t.length() < 5) {
				continue loop;
			}
			final String createParts = t.replaceAll("\\((\\d+,\\d+)\\)", "").replaceAll("\\((\\d+)\\)", "");//.split("\\(");
			final String tableName = createParts.substring(0, createParts.indexOf("("));
			final Table table = new Table(tableName.trim());
			final List<String> pks = new ArrayList<>();
			innerloop : for (final String c : createParts.substring(createParts.indexOf("("), createParts.length()-1).split("\\r?\\n")) {
				if (c.trim().startsWith("--") || c.trim().isEmpty() || ");".equals(c.trim()) || "(".equals(c.trim())) {
					continue innerloop;
				}
				if (c.trim().startsWith("primary key")) {
					if (c.contains(",")) {
						pks.addAll(Arrays.asList(c.replaceAll("primary key", "").split("\\(")[1].split(",")).stream().map(x -> x.trim().replaceAll("\\)", "")).collect(Collectors.toList()));
					} else {
						pks.add(c.replaceAll("primary key", "").replaceAll("\\(", "").replaceAll("\\)", "").trim());
					}
					continue innerloop;
				}
				String columnString = c.replaceAll(";", "");
				final Column column = new Column();
				column.setNullable(!c.contains("not null"));
				columnString = columnString.replaceAll("not null", "").replaceAll("\\((d+,d+)\\)", "");
				final String columnName = columnString.trim().substring(0, columnString.trim().indexOf(' ')).trim();
				column.setColumnName(columnName);
				columnString = columnString.replaceAll(columnName, "").replaceAll(",", "");
				column.setType(columnString.trim().substring(0, columnString.trim().length()).trim());
				table.addColumn(column);
			}
			table.setPrimaryKeys(pks);

			tables.add(table);
		}

		return tables;
	}

	private void demo () throws Exception {
		final Client client = TransportClient
				.builder()
				.build()
				.addTransportAddress(
						new InetSocketTransportAddress(InetAddress
								.getByName("localhost"), 9300));

		Spark.get("/", (request, response) -> {
			final SearchResponse searchResponse = client.prepareSearch("music")
					.setTypes("lyrics").execute().actionGet();
			final SearchHit[] hits = searchResponse.getHits().getHits();

			final Map<String, Object> attributes = new HashMap<>();
			attributes.put("songs", hits);

			return new ModelAndView(attributes, "index.mustache");
		}, new MustacheTemplateEngine());
		Spark.get(
				"/search",
				(request, response) -> {
					final SearchRequestBuilder srb = client.prepareSearch("music")
							.setTypes("lyrics");

					final String lyricParam = request.queryParams("query");
					QueryBuilder lyricQuery = null;
					if (lyricParam != null && lyricParam.trim().length() > 0) {
						lyricQuery = QueryBuilders.matchQuery("lyrics",
								lyricParam);
						// srb.setQuery(qb).addHighlightedField("lyrics", 0, 0);
					}
					final String artistParam = request.queryParams("artist");
					QueryBuilder artistQuery = null;
					if (artistParam != null && artistParam.trim().length() > 0) {
						artistQuery = QueryBuilders.matchQuery("artist",
								artistParam);
					}

					if (lyricQuery != null && artistQuery == null) {
						srb.setQuery(lyricQuery).addHighlightedField("lyrics",
								0, 0);
					} else if (lyricQuery == null && artistQuery != null) {
						srb.setQuery(artistQuery);
					} else if (lyricQuery != null && artistQuery != null) {
						srb.setQuery(
								QueryBuilders.andQuery(artistQuery, lyricQuery))
								.addHighlightedField("lyrics", 0, 0);
					}

					final SearchResponse searchResponse = srb.execute().actionGet();

					final SearchHit[] hits = searchResponse.getHits().getHits();

					final Map<String, Object> attributes = new HashMap<>();
					attributes.put("songs", hits);

					return new ModelAndView(attributes, "index.mustache");
				}, new MustacheTemplateEngine());
		Spark.get("/add", (request, response) -> {
			return new ModelAndView(new HashMap(), "add.mustache");
		}, new MustacheTemplateEngine());
		Spark.post("/save", (request, response) -> {
			final StringBuilder json = new StringBuilder("{");
			json.append("\"name\":\""+request.raw().getParameter("name")+"\",");
			json.append("\"artist\":\""+request.raw().getParameter("artist")+"\",");
			json.append("\"year\":"+request.raw().getParameter("year")+",");
			json.append("\"album\":\""+request.raw().getParameter("album")+"\",");
			json.append("\"lyrics\":\""+request.raw().getParameter("lyrics")+"\"}");

			final IndexRequest indexRequest = new IndexRequest("music", "lyrics",
					UUID.randomUUID().toString());
			indexRequest.source(json.toString());
			final IndexResponse esResponse = client.index(indexRequest).actionGet();

			final Map<String, Object> attributes = new HashMap<>();
			return new ModelAndView(attributes, "index.mustache");
		}, new MustacheTemplateEngine());
	}
}
