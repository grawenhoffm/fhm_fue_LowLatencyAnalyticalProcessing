package de.mk.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import de.mk.elasticsearch.tableobjects.Column;
import de.mk.elasticsearch.tableobjects.Table;

public class LogstashImporter {

	private static final String CSV_FILE_NAME_PLACEHOLDER = "###FILE_NAME###";
	private static final String PK_NAME_PLACEHOLDER = "###PK_NAME###";
	private static final String COLUMNS_PLACEHOLDER = "###COLUMN_NAMES###";
	private static final String COLUMN_CONVERTION = "###COLUMN_CONVERT###";
	private static final String PATH_CONVERTION = "###PATH_CONVERT###";
	private static final String LOGSTASH_CSV_PATH = "logstash-template-mk.conf";
//	private static final String LOGSTASH_CSV_PATH = "logstash-template-sm.conf";

	private static Map<String, String> filedMapper = new HashMap<String, String>() {
		private static final long serialVersionUID = -5600380536606597600L;

		{
//			this.put("decimal", "double");
//			this.put("integer", "long");
//			this.put("char", "text");
//			this.put("varchar", "text");
//			this.put("date", "date");
//			this.put("time", null);
//			this.put("boolean", "boolean");

			this.put("decimal", "float");
			this.put("integer", "integer");
			//this.put("char", "text");
			//this.put("varchar", "text");
			this.put("date", "date");
			//this.put("time", null);
			this.put("boolean", "boolean");
		}
	};

	//"c_customer_sk","c_customer_id (B)","c_current_cdemo_sk","c_current_hdemo_sk","c_current_addr_sk","c_first_shipto_date_sk","c_first_sales_date_sk","c_salutation","c_first_name","c_last_name","c_preferred_cust_flag","c_birth_day","c_birth_month","c_birth_year","c_birth_country","c_login","c_email_address","c_last_review_date_sk"
	public void generateLogstashImport(List<Table> tables) throws IOException {
		final String template = this.getFileAsString(LOGSTASH_CSV_PATH);
//		final String template = this.getInventoryJoinQuery(LOGSTASH_CSV_PATH);
		final StringBuffer sparkString = new StringBuffer();
		for (final Table t : tables) {
//			if (t.getPrimaryKeyColumnNames().size() == 1) {
			String filledTemplate = template.replaceAll(CSV_FILE_NAME_PLACEHOLDER, t.getName());


			sparkString.append("final Dataset<Row> "+t.getName()+"rows = sqlContext.sql(\"CREATE TEMPORARY VIEW " + t.getName() + " (");
			//filledTemplate = filledTemplate.replaceAll(PK_NAME_PLACEHOLDER, t.getPrimaryKeyColumnNames().get(0));
			final StringBuffer buf = new StringBuffer();
			final StringBuffer sparkColumns = new StringBuffer();
			String type = null;
			for (final Column c : t.getColumns()) {
				buf.append(",\"").append(c.getColumnName()).append("\"");
				type = c.getType();
				if (type.contains("char")) {
					type = "String";
				}
				sparkColumns.append(", " + c.getColumnName() + " " + type);
			}
			sparkString.append(sparkColumns.toString().replaceFirst(",", ""));
			filledTemplate = filledTemplate.replaceAll(COLUMNS_PLACEHOLDER, buf.toString().replaceFirst(",", ""));
			sparkString.append(") USING com.databricks.spark.csv OPTIONS (path '\"+pathtocsvs+\"/"+t.getName()+".dat', header 'false', mode 'FAILFAST', delimiter '|')\");\n");
			final StringBuffer columnConverter = new StringBuffer();
			String columnType = null;
			for (final Column c : t.getColumns()) {
				columnType = filedMapper.get(c.getType().trim());
				if (columnType != null) {
					columnConverter.append("\"").append(c.getColumnName()).append("\"").append(" => ").append("\"").append(columnType).append("\" \n");
				}
			}
			filledTemplate = filledTemplate.replaceAll(COLUMN_CONVERTION, columnConverter.toString());
//			filledTemplate = template.replaceAll(PATH_CONVERTION, localCsvPath);
			System.out.println(filledTemplate);
			FileUtils.writeStringToFile(new File("logstash_conf/logstash-"+t.getName()+".conf"), filledTemplate, Charset.forName("UTF-8"));
		}
		//System.out.println(sparkString.toString());
//		}

		this.printSQLSelectFields(tables);

	}

	// Generiere den Teil des Selects, da SELECT * FROM nicht funktioniert mit Spark
	private void printSQLSelectFields(List<Table> tables) throws FileNotFoundException, IOException {
		final List<String> sqlFileNames = new ArrayList<String>() {{
			//this.add("sql/inventory_fullouterjoin.sql");
			this.add("sql/web_fullouterjoin.sql");
			this.add("sql/catalog_fullouterjoin.sql");
			this.add("sql/store_fullouterjoin.sql");
		}};

		for (final String s : sqlFileNames) {
			final String sqlFile = this.getFileAsString(s).toLowerCase();
			final List<TableAliasMapper> tableToAlias = new ArrayList<>(); // KEY -> Tabellenname ; VALUE -> Tabellenalias

			// FROM
			final String fromString = sqlFile.substring(sqlFile.indexOf(" from "), sqlFile.indexOf(" join ")).replace("from", "").trim();
			if (fromString.split(" ")[1].trim().indexOf("\n") > -1) {
				tableToAlias.add(new TableAliasMapper(fromString.split(" ")[0].trim(), fromString.split(" ")[1].trim().substring(0, fromString.split(" ")[1].trim().indexOf("\n"))));
			} else {
				tableToAlias.add(new TableAliasMapper(fromString.split(" ")[0].trim(), fromString.split(" ")[1].trim()));
			}

			//JOIN
			final String[] joinArray = sqlFile.split("join ");
			for (int i = 1; i< joinArray.length; i++) { // Ignoriere den ersten Eintrag, hier steht das FROM drin
				tableToAlias.add(new TableAliasMapper(joinArray[i].split(" ")[0].trim(), joinArray[i].split(" ")[1].trim().split(" ")[0]));
			}

			System.out.println(s + " \n");
			final StringBuilder bui = new StringBuilder();
			for (final TableAliasMapper e : tableToAlias) {
				// Finde alle Spaltennamen der Tabelle

				final Table t = tables.stream().filter(x -> x.getName().equals(e.getTable())).findFirst().get();
				for (final Column c : t.getColumns()) {
					bui.append(", ");
					bui.append(e.getAlias()).append(".").append(c.getColumnName()).append(" AS ").append(e.getAlias()).append("_").append(c.getColumnName());
				}
			}
			System.out.println(bui.toString().replaceFirst(", ", ""));
			System.out.println("\n\n");
		}
	}

	private String getFileAsString(String filePath) throws FileNotFoundException, IOException {
		final StringBuffer buf = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if (!line.trim().isEmpty() && !"".equals(line.trim())) {
		    		buf.append(line + "\n");
		    	}
		    }
		    return buf.toString();
		} catch (final Exception e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

	private class TableAliasMapper {
		private String table;
		private String alias;
		public TableAliasMapper(String table, String alias) {
			this.table = table;
			this.alias = alias;
		}
		public String getTable() {
			return this.table;
		}
		public void setTable(String table) {
			this.table = table;
		}
		public String getAlias() {
			return this.alias;
		}
		public void setAlias(String alias) {
			this.alias = alias;
		}


	}
}
