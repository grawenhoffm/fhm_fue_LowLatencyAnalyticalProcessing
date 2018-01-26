package de.funde.elasticsearch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.JavaConverters;
import scala.collection.Seq;



public class SparkImporter implements Serializable {

	private static final long serialVersionUID = -7486646526559176862L;
	private static final Logger LOG = LoggerFactory.getLogger(SparkImporter.class);

	public static void main(String[] args) throws FileNotFoundException, IOException {

		boolean withES = false;
		String indexName = "tpcdsstore/docs";
		String pathtocsvs = "/data/tpcds/1g";
		boolean withCsv = false;
		String csvName = "/data/tpcds/store_filtered_join.csv";


		if (args.length == 3) {
			withES = Boolean.valueOf(args[0]).booleanValue();
			indexName = args[1];
			pathtocsvs = args[2];
		} else if (args.length == 5) {
			withES = Boolean.valueOf(args[0]).booleanValue();
			indexName = args[1];
			pathtocsvs = args[2];
			withCsv = Boolean.valueOf(args[3]).booleanValue();
			csvName = args[4];
		} else {
			LOG.error("Nicht genügend Parameter übergeben. \n 0. Sollen die Daten nach ES geschrieben werden? (Boolean) \n 1. ES-IndexName/DocName \n 2. Soll eine CSV erzeugt werden? (Boolean) \n 3. Pfad, indem die neue CSV abgelegt werden soll.");
			System.exit(5);
		}
		final SparkConf conf = new SparkConf().setAppName("MK-Test");//.setMaster("local[2]");

		conf.set("es.resource", indexName); //
		conf.set("es.nodes", "10.42.13.206");
		//conf.set("es.nodes", "10.60.64.84");
		conf.set("es.port", "9204");
		conf.set("es.nodes.wan.only", "true");

		// ###
		// ### Achtung
		// ###
		conf.set("es.index.auto.create", "true");


		//final JavaSparkContext sc = new JavaSparkContext(conf);
		final SparkSession session = SparkSession.builder().config(conf).getOrCreate();

		final SQLContext sqlContext = new SQLContext(session);

//		final String pathtocsvs = "/home/spark/spark-tpc-ds-performance-test/gendata";


		final Dataset<Row> customer_addressrows = sqlContext.sql("CREATE TEMPORARY VIEW customer_address ( ca_address_sk integer, ca_address_id String, ca_street_number String, ca_street_name String, ca_street_type String, ca_suite_number String, ca_city String, ca_county String, ca_state String, ca_zip String, ca_country String, ca_gmt_offset decimal, ca_location_type String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/customer_address.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> customer_demographicsrows = sqlContext.sql("CREATE TEMPORARY VIEW customer_demographics ( cd_demo_sk integer, cd_gender String, cd_marital_status String, cd_education_status String, cd_purchase_estimate integer, cd_credit_rating String, cd_dep_count integer, cd_dep_employed_count integer, cd_dep_college_count integer) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/customer_demographics.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> date_dimrows = sqlContext.sql("CREATE TEMPORARY VIEW date_dim ( d_date_sk integer, d_date_id String, d_date date, d_month_seq integer, d_week_seq integer, d_quarter_seq integer, d_year integer, d_dow integer, d_moy integer, d_dom integer, d_qoy integer, d_fy_year integer, d_fy_quarter_seq integer, d_fy_week_seq integer, d_day_name String, d_quarter_name String, d_holiday String, d_weekend String, d_following_holiday String, d_first_dom integer, d_last_dom integer, d_same_day_ly integer, d_same_day_lq integer, d_current_day String, d_current_week String, d_current_month String, d_current_quarter String, d_current_year String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/date_dim.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> warehouserows = sqlContext.sql("CREATE TEMPORARY VIEW warehouse ( w_warehouse_sk integer, w_warehouse_id String, w_warehouse_name String, w_warehouse_sq_ft integer, w_street_number String, w_street_name String, w_street_type String, w_suite_number String, w_city String, w_county String, w_state String, w_zip String, w_country String, w_gmt_offset decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/warehouse.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> ship_moderows = sqlContext.sql("CREATE TEMPORARY VIEW ship_mode ( sm_ship_mode_sk integer, sm_ship_mode_id String, sm_type String, sm_code String, sm_carrier String, sm_contract String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/ship_mode.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> time_dimrows = sqlContext.sql("CREATE TEMPORARY VIEW time_dim ( t_time_sk integer, t_time_id String, t_time integer, t_hour integer, t_minute integer, t_second integer, t_am_pm String, t_shift String, t_sub_shift String, t_meal_time String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/time_dim.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> reasonrows = sqlContext.sql("CREATE TEMPORARY VIEW reason ( r_reason_sk integer, r_reason_id String, r_reason_desc String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/reason.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> income_bandrows = sqlContext.sql("CREATE TEMPORARY VIEW income_band ( ib_income_band_sk integer, ib_lower_bound integer, ib_upper_bound integer) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/income_band.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> itemrows = sqlContext.sql("CREATE TEMPORARY VIEW item ( i_item_sk integer, i_item_id String, i_rec_start_date date, i_rec_end_date date, i_item_desc String, i_current_price decimal, i_wholesale_cost decimal, i_brand_id integer, i_brand String, i_class_id integer, i_class String, i_category_id integer, i_category String, i_manufact_id integer, i_manufact String, i_size String, i_formulation String, i_color String, i_units String, i_container String, i_manager_id integer, i_product_name String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/item.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> storerows = sqlContext.sql("CREATE TEMPORARY VIEW store ( s_store_sk integer, s_store_id String, s_rec_start_date date, s_rec_end_date date, s_closed_date_sk integer, s_store_name String, s_number_employees integer, s_floor_space integer, s_hours String, s_manager String, s_market_id integer, s_geography_class String, s_market_desc String, s_market_manager String, s_division_id integer, s_division_name String, s_company_id integer, s_company_name String, s_street_number String, s_street_name String, s_street_type String, s_suite_number String, s_city String, s_county String, s_state String, s_zip String, s_country String, s_gmt_offset decimal, s_tax_precentage decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/store.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> call_centerrows = sqlContext.sql("CREATE TEMPORARY VIEW call_center ( cc_call_center_sk integer, cc_call_center_id String, cc_rec_start_date date, cc_rec_end_date date, cc_closed_date_sk integer, cc_open_date_sk integer, cc_name String, cc_class String, cc_employees integer, cc_sq_ft integer, cc_hours String, cc_manager String, cc_mkt_id integer, cc_mkt_class String, cc_mkt_desc String, cc_market_manager String, cc_division integer, cc_division_name String, cc_company integer, cc_company_name String, cc_street_number String, cc_street_name String, cc_street_type String, cc_suite_number String, cc_city String, cc_county String, cc_state String, cc_zip String, cc_country String, cc_gmt_offset decimal, cc_tax_percentage decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/call_center.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> customerrows = sqlContext.sql("CREATE TEMPORARY VIEW customer ( c_customer_sk integer, c_customer_id String, c_current_cdemo_sk integer, c_current_hdemo_sk integer, c_current_addr_sk integer, c_first_shipto_date_sk integer, c_first_sales_date_sk integer, c_salutation String, c_first_name String, c_last_name String, c_preferred_cust_flag String, c_birth_day integer, c_birth_month integer, c_birth_year integer, c_birth_country String, c_login String, c_email_address String, c_last_review_date String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/customer.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> web_siterows = sqlContext.sql("CREATE TEMPORARY VIEW web_site ( web_site_sk integer, web_site_id String, web_rec_start_date date, web_rec_end_date date, web_name String, web_open_date_sk integer, web_close_date_sk integer, web_class String, web_manager String, web_mkt_id integer, web_mkt_class String, web_mkt_desc String, web_market_manager String, web_company_id integer, web_company_name String, web_street_number String, web_street_name String, web_street_type String, web_suite_number String, web_city String, web_county String, web_state String, web_zip String, web_country String, web_gmt_offset decimal, web_tax_percentage decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/web_site.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> store_returnsrows = sqlContext.sql("CREATE TEMPORARY VIEW store_returns ( sr_returned_date_sk integer, sr_return_time_sk integer, sr_item_sk integer, sr_customer_sk integer, sr_cdemo_sk integer, sr_hdemo_sk integer, sr_addr_sk integer, sr_store_sk integer, sr_reason_sk integer, sr_ticket_number integer, sr_return_quantity integer, sr_return_amt decimal, sr_return_tax decimal, sr_return_amt_inc_tax decimal, sr_fee decimal, sr_return_ship_cost decimal, sr_refunded_cash decimal, sr_reversed_charge decimal, sr_store_credit decimal, sr_net_loss decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/store_returns.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> household_demographicsrows = sqlContext.sql("CREATE TEMPORARY VIEW household_demographics ( hd_demo_sk integer, hd_income_band_sk integer, hd_buy_potential String, hd_dep_count integer, hd_vehicle_count integer) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/household_demographics.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> web_pagerows = sqlContext.sql("CREATE TEMPORARY VIEW web_page ( wp_web_page_sk integer, wp_web_page_id String, wp_rec_start_date date, wp_rec_end_date date, wp_creation_date_sk integer, wp_access_date_sk integer, wp_autogen_flag String, wp_customer_sk integer, wp_url String, wp_type String, wp_char_count integer, wp_link_count integer, wp_image_count integer, wp_max_ad_count integer) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/web_page.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> promotionrows = sqlContext.sql("CREATE TEMPORARY VIEW promotion ( p_promo_sk integer, p_promo_id String, p_start_date_sk integer, p_end_date_sk integer, p_item_sk integer, p_cost decimal, p_response_target integer, p_promo_name String, p_channel_dmail String, p_channel_email String, p_channel_catalog String, p_channel_tv String, p_channel_radio String, p_channel_press String, p_channel_event String, p_channel_demo String, p_channel_details String, p_purpose String, p_discount_active String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/promotion.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> catalog_pagerows = sqlContext.sql("CREATE TEMPORARY VIEW catalog_page ( cp_catalog_page_sk integer, cp_catalog_page_id String, cp_start_date_sk integer, cp_end_date_sk integer, cp_department String, cp_catalog_number integer, cp_catalog_page_number integer, cp_description String, cp_type String) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/catalog_page.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> inventoryrows = sqlContext.sql("CREATE TEMPORARY VIEW inventory ( inv_date_sk integer, inv_item_sk integer, inv_warehouse_sk integer, inv_quantity_on_hand integer) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/inventory.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> catalog_returnsrows = sqlContext.sql("CREATE TEMPORARY VIEW catalog_returns ( cr_returned_date_sk integer, cr_returned_time_sk integer, cr_item_sk integer, cr_refunded_customer_sk integer, cr_refunded_cdemo_sk integer, cr_refunded_hdemo_sk integer, cr_refunded_addr_sk integer, cr_returning_customer_sk integer, cr_returning_cdemo_sk integer, cr_returning_hdemo_sk integer, cr_returning_addr_sk integer, cr_call_center_sk integer, cr_catalog_page_sk integer, cr_ship_mode_sk integer, cr_warehouse_sk integer, cr_reason_sk integer, cr_order_number integer, cr_return_quantity integer, cr_return_amount decimal, cr_return_tax decimal, cr_return_amt_inc_tax decimal, cr_fee decimal, cr_return_ship_cost decimal, cr_refunded_cash decimal, cr_reversed_charge decimal, cr_store_credit decimal, cr_net_loss decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/catalog_returns.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> web_returnsrows = sqlContext.sql("CREATE TEMPORARY VIEW web_returns ( wr_returned_date_sk integer, wr_returned_time_sk integer, wr_item_sk integer, wr_refunded_customer_sk integer, wr_refunded_cdemo_sk integer, wr_refunded_hdemo_sk integer, wr_refunded_addr_sk integer, wr_returning_customer_sk integer, wr_returning_cdemo_sk integer, wr_returning_hdemo_sk integer, wr_returning_addr_sk integer, wr_web_page_sk integer, wr_reason_sk integer, wr_order_number integer, wr_return_quantity integer, wr_return_amt decimal, wr_return_tax decimal, wr_return_amt_inc_tax decimal, wr_fee decimal, wr_return_ship_cost decimal, wr_refunded_cash decimal, wr_reversed_charge decimal, wr_account_credit decimal, wr_net_loss decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/web_returns.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> web_salesrows = sqlContext.sql("CREATE TEMPORARY VIEW web_sales ( ws_sold_date_sk integer, ws_sold_time_sk integer, ws_ship_date_sk integer, ws_item_sk integer, ws_bill_customer_sk integer, ws_bill_cdemo_sk integer, ws_bill_hdemo_sk integer, ws_bill_addr_sk integer, ws_ship_customer_sk integer, ws_ship_cdemo_sk integer, ws_ship_hdemo_sk integer, ws_ship_addr_sk integer, ws_web_page_sk integer, ws_web_site_sk integer, ws_ship_mode_sk integer, ws_warehouse_sk integer, ws_promo_sk integer, ws_order_number integer, ws_quantity integer, ws_wholesale_cost decimal, ws_list_price decimal, ws_sales_price decimal, ws_ext_discount_amt decimal, ws_ext_sales_price decimal, ws_ext_wholesale_cost decimal, ws_ext_list_price decimal, ws_ext_tax decimal, ws_coupon_amt decimal, ws_ext_ship_cost decimal, ws_net_paid decimal, ws_net_paid_inc_tax decimal, ws_net_paid_inc_ship decimal, ws_net_paid_inc_ship_tax decimal, ws_net_profit decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/web_sales.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> catalog_salesrows = sqlContext.sql("CREATE TEMPORARY VIEW catalog_sales ( cs_sold_date_sk integer, cs_sold_time_sk integer, cs_ship_date_sk integer, cs_bill_customer_sk integer, cs_bill_cdemo_sk integer, cs_bill_hdemo_sk integer, cs_bill_addr_sk integer, cs_ship_customer_sk integer, cs_ship_cdemo_sk integer, cs_ship_hdemo_sk integer, cs_ship_addr_sk integer, cs_call_center_sk integer, cs_catalog_page_sk integer, cs_ship_mode_sk integer, cs_warehouse_sk integer, cs_item_sk integer, cs_promo_sk integer, cs_order_number integer, cs_quantity integer, cs_wholesale_cost decimal, cs_list_price decimal, cs_sales_price decimal, cs_ext_discount_amt decimal, cs_ext_sales_price decimal, cs_ext_wholesale_cost decimal, cs_ext_list_price decimal, cs_ext_tax decimal, cs_coupon_amt decimal, cs_ext_ship_cost decimal, cs_net_paid decimal, cs_net_paid_inc_tax decimal, cs_net_paid_inc_ship decimal, cs_net_paid_inc_ship_tax decimal, cs_net_profit decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/catalog_sales.csv', header 'false', mode 'FAILFAST', delimiter '|')");
		final Dataset<Row> store_salesrows = sqlContext.sql("CREATE TEMPORARY VIEW store_sales ( ss_sold_date_sk integer, ss_sold_time_sk integer, ss_item_sk integer, ss_customer_sk integer, ss_cdemo_sk integer, ss_hdemo_sk integer, ss_addr_sk integer, ss_store_sk integer, ss_promo_sk integer, ss_ticket_number integer, ss_quantity integer, ss_wholesale_cost decimal, ss_list_price decimal, ss_sales_price decimal, ss_ext_discount_amt decimal, ss_ext_sales_price decimal, ss_ext_wholesale_cost decimal, ss_ext_list_price decimal, ss_ext_tax decimal, ss_coupon_amt decimal, ss_net_paid decimal, ss_net_paid_inc_tax decimal, ss_net_profit decimal) USING com.databricks.spark.csv OPTIONS (path '"+pathtocsvs+"/store_sales.csv', header 'false', mode 'FAILFAST', delimiter '|')");



		LOG.error("------------------------- Test-Daten geschrieben -------------------------");
		//final ImmutableList.Builder<scala.collection.immutable.Map<String, ?>> builder = new ImmutableList.Builder<scala.collection.immutable.Map<String, ?>>();


		// ### Inventory JOIN Nach ES
		//Dataset<Row> inventoryData = sqlContext.sql(getInventoryJoinQuery("inventory_fullouterjoin.sql"));
		//final JavaRDD<scala.collection.immutable.Map<String, ?>> inventoryRDD = mapTORDD(inventoryData);
		//JavaEsSpark.saveToEs(inventoryRDD, "inventory/docs");
		//LOG.error("------------------------- Inventory nach ES geschrieben -------------------------");
		//inventoryData.coalesce(1).write().format("com.databricks.spark.csv").option("header", "true").save("/home/elastic/mk/inventory_full_outer.csv");
		//LOG.error("------------------------- Inventory nach CSV geschrieben -------------------------");
		//inventoryData = null;

//		Dataset<Row> webData = sqlContext.sql(getInventoryJoinQuery("web_fullouterjoin.sql") + " LIMIT 10");
//		JavaEsSpark.saveToEs(mapTORDD(webData), "web/docs");
//		LOG.error("------------------------- Web nach ES geschrieben -------------------------");
//		webData.coalesce(1).write().format("com.databricks.spark.csv").option("header", "true").save("/home/elastic/mk/web_full_outer.csv");
//		LOG.error("------------------------- Web nach CSV geschrieben -------------------------");
//		webData = null;
//
//		Dataset<Row> catalogData = sqlContext.sql(getInventoryJoinQuery("catalog_fullouterjoin.sql") + " LIMIT 10");
//		JavaEsSpark.saveToEs(mapTORDD(catalogData), "catalog/docs");
//		LOG.error("------------------------- Catalog nach ES geschrieben -------------------------");
//		catalogData.coalesce(1).write().format("com.databricks.spark.csv").option("header", "true").save("/home/elastic/mk/catalog_full_outer.csv");
//		LOG.error("------------------------- Catalog nach CSV geschrieben -------------------------");
//		catalogData = null;

		if (withES) {
			Dataset<Row> storeDataES = sqlContext.sql(getInventoryJoinQuery("store_sales_filtered_join.sql"));
			LOG.error("JOIN fuer ES durchgefuehrt.");
			JavaEsSpark.saveToEs(mapTORDD(storeDataES), indexName);
			storeDataES = null;
			LOG.error("------------------------- Store nach ES geschrieben -------------------------");
		} else {
			LOG.error("Kein Schreiben nach ES gewünscht");
		}
		if (withCsv) {
			Dataset<Row> storeData = sqlContext.sql(getInventoryJoinQuery("store_sales_filtered_join_csv.sql"));
			LOG.error("JOIN fuer CSV durchgefuehrt.");
			storeData.coalesce(1).write().format("com.databricks.spark.csv").option("header", "true").save(csvName);
			LOG.error("------------------------- Store nach CSV geschrieben -------------------------");
			storeData = null;
		} else {
			LOG.error("Kein Schreiben zur CSV gewünscht");
		}

		session.close();
	}

	private static JavaRDD<scala.collection.mutable.Map<String, Object>> mapTORDD(Dataset<Row>  dataset) {

		final JavaRDD<scala.collection.mutable.Map<String, Object>> resultRDD = dataset.toJavaRDD().map(x-> {
			final Seq<String> fieldNames = JavaConverters.asScalaIteratorConverter(Arrays.asList(x.schema().fieldNames()).iterator()).asScala().toSeq(); // Suche alle Feldnamen raus
			final scala.collection.immutable.Map<String, ?> localRowData = x.getValuesMap(fieldNames); // Packe alle Feldnamen als Key und die Values als Value in die Map
//			final DataType d = x.schema().fields()[0].dataType();
//			for (final StructField f : x.schema().fields()) {
//				if (f.dataType().getClass().equals(StringType.class)) {
//					if (null == (Object) localRowData.get(f.name())) {
//
//					}
//
//
//				}
//			}
			// Lösche null-Werte
			final java.util.HashMap<String, Object> javaMap = new java.util.HashMap<String, Object>(
	                JavaConverters.mapAsJavaMapConverter(localRowData).asJava());

			for(final Iterator<java.util.Map.Entry<String, Object>> it = javaMap.entrySet().iterator(); it.hasNext(); ) {
				final java.util.Map.Entry<String, Object> entry = it.next();
			    if(entry.getValue() == null || "null".equals(String.valueOf(entry.getValue()))) {
			    	it.remove();
			    }
			}

			return scala.collection.JavaConverters.mapAsScalaMapConverter(javaMap).asScala();


			//return localRowData;
		});

		return resultRDD;
	}

	private static String getInventoryJoinQuery(String fileName) throws FileNotFoundException, IOException {
		final StringBuffer buf = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if (!line.trim().isEmpty() && !"".equals(line.trim())) {
		    		buf.append(line + "\n");
		    	}
		    }
		    return buf.toString();
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return "";
	}
}
