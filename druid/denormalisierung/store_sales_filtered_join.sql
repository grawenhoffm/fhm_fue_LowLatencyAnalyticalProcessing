SELECT concat(ss_sold_date.d_date,'T',ss_sold_time.t_hour,':',ss_sold_time.t_minute,':',ss_sold_time.t_second,'.000Z') AS timestamp,
ss.ss_sold_date_sk AS ss_ss_sold_date_sk,
ss.ss_sold_time_sk AS ss_ss_sold_time_sk,
ss.ss_item_sk AS ss_ss_item_sk,
ss.ss_customer_sk AS ss_ss_customer_sk,
ss.ss_cdemo_sk AS ss_ss_cdemo_sk,
ss.ss_hdemo_sk AS ss_ss_hdemo_sk,
ss.ss_addr_sk AS ss_ss_addr_sk,
ss.ss_store_sk AS ss_ss_store_sk,
ss.ss_promo_sk AS ss_ss_promo_sk,
ss.ss_quantity AS ss_ss_quantity,
ss.ss_list_price AS ss_ss_list_price,
ss.ss_sales_price AS ss_ss_sales_price,
ss.ss_ext_sales_price AS ss_ss_ext_sales_price,
ss.ss_ext_wholesale_cost AS ss_ss_ext_wholesale_cost,
ss.ss_coupon_amt AS ss_ss_coupon_amt,
ss.ss_net_profit AS ss_ss_net_profit,
ss_sold_date.d_date_sk AS ss_sold_date_d_date_sk,
ss_sold_date.d_date AS ss_sold_date_d_date,
ss_sold_date.d_month_seq AS ss_sold_date_d_month_seq,
ss_sold_date.d_year AS ss_sold_date_d_year,
ss_sold_date.d_moy AS ss_sold_date_d_moy,
ss_sold_date.d_day_name AS ss_sold_date_d_day_name,
ss_sold_time.t_time_sk AS ss_sold_time_t_time_sk,
ss_sold_time.t_time AS ss_sold_time_t_time,
ss_sold_time.t_hour AS ss_sold_time_t_hour,
ss_sold_time.t_minute AS ss_sold_time_t_minute,
ss_sold_time.t_second AS ss_sold_time_t_second,
ss_item.i_item_sk AS ss_item_i_item_sk,
ss_item.i_item_id AS ss_item_i_item_id,
ss_item.i_item_desc AS ss_item_i_item_desc,
ss_item.i_current_price AS ss_item_i_current_price,
ss_item.i_brand_id AS ss_item_i_brand_id,
ss_item.i_brand AS ss_item_i_brand,
ss_item.i_class AS ss_item_i_class,
ss_item.i_category_id AS ss_item_i_category_id,
ss_item.i_category AS ss_item_i_category,
ss_item.i_manufact_id AS ss_item_i_manufact_id,
ss_item.i_manufact AS ss_item_i_manufact,
ss_item.i_size AS ss_item_i_size,
ss_item.i_manager_id AS ss_item_i_manager_id,
ss_customer.c_customer_sk AS ss_customer_c_customer_sk,
ss_customer.c_current_addr_sk AS ss_customer_c_current_addr_sk,
ss_cdemo.cd_demo_sk AS ss_cdemo_cd_demo_sk,
ss_cdemo.cd_gender AS ss_cdemo_cd_gender,
ss_cdemo.cd_marital_status AS ss_cdemo_cd_marital_status,
ss_cdemo.cd_education_status AS ss_cdemo_cd_education_status,
ss_hdemo.hd_demo_sk AS ss_hdemo_hd_demo_sk,
ss_hdemo.hd_dep_count AS ss_hdemo_hd_dep_count,
ss_addr.ca_address_sk AS ss_addr_ca_address_sk,
ss_addr.ca_state AS ss_addr_ca_state,
ss_addr.ca_zip AS ss_addr_ca_zip,
ss_addr.ca_country AS ss_addr_ca_country,
ss_promo.p_promo_sk AS ss_promo_p_promo_sk,
ss_promo.p_channel_email AS ss_promo_p_channel_email,
ss_promo.p_channel_event AS ss_promo_p_channel_event,
ss_store.s_store_sk AS ss_store_s_store_sk,
ss_store.s_store_id AS ss_store_s_store_id,
ss_store.s_store_name AS ss_store_s_store_name,
ss_store.s_county AS ss_store_s_county,
ss_store.s_state AS ss_store_s_state,
ss_store.s_zip AS ss_store_s_zip,
ss_store.s_gmt_offset AS ss_store_s_gmt_offset,
ss_c_current_addr.ca_address_sk AS ss_c_current_addr_ca_address_sk,
ss_c_current_addr.ca_state AS ss_c_current_addr_ca_state,
ss_c_current_addr.ca_zip AS ss_c_current_addr_ca_zip,
ss_c_current_addr.ca_country AS ss_c_current_addr_ca_country

FROM store_sales as,
date_dim as ss_sold_date,
time_dim as ss_sold_time,
item as ss_item AS,
customer as ss_customer,
customer_demographics as,
household_demographics as,
customer_address as ss_addr,
promotion as ss_promo,
store as ss_store,
customer_address as ss_c_current_addr

WHERE ss.ss_sold_date_sk = ss_sold_date.d_date_sk
AND ss.ss_sold_time_sk = ss_sold_time.t_time_sk
AND ss.ss_item_sk = ss_item.i_item_sk
AND ss.ss_customer_sk = ss_customer.c_customer_sk
AND ss.ss_cdemo_sk = ss_cdemo.cd_demo_sk
AND ss.ss_hdemo_sk = ss_hdemo.hd_demo_sk
AND ss.ss_addr_sk = ss_addr.ca_address_sk
AND ss.ss_promo_sk = ss_promo.p_promo_sk
AND ss.ss_store_sk = ss_store.s_store_sk

/* Customer */
AND ss_customer.c_current_addr_sk = ss_c_current_addr.ca_address_sk

/* LIMIT 100 */
;
