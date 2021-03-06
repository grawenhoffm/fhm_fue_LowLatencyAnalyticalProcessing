SELECT
ss.ss_sold_date_sk AS ss_ss_sold_date_sk, 
ss.ss_sold_time_sk AS ss_ss_sold_time_sk, 
ss.ss_item_sk AS ss_ss_item_sk, 
ss.ss_customer_sk AS ss_ss_customer_sk, 
ss.ss_cdemo_sk AS ss_ss_cdemo_sk, 
ss.ss_hdemo_sk AS ss_ss_hdemo_sk, 
ss.ss_addr_sk AS ss_ss_addr_sk, 
ss.ss_store_sk AS ss_ss_store_sk, 
ss.ss_promo_sk AS ss_ss_promo_sk, 
ss.ss_ticket_number AS ss_ss_ticket_number, 
ss.ss_quantity AS ss_ss_quantity, 
ss.ss_wholesale_cost AS ss_ss_wholesale_cost, 
ss.ss_list_price AS ss_ss_list_price, 
ss.ss_sales_price AS ss_ss_sales_price, 
ss.ss_ext_discount_amt AS ss_ss_ext_discount_amt, 
ss.ss_ext_sales_price AS ss_ss_ext_sales_price, 
ss.ss_ext_wholesale_cost AS ss_ss_ext_wholesale_cost, 
ss.ss_ext_list_price AS ss_ss_ext_list_price, 
ss.ss_ext_tax AS ss_ss_ext_tax, ss.ss_coupon_amt AS 
ss_ss_coupon_amt, ss.ss_net_paid AS ss_ss_net_paid, 
ss.ss_net_paid_inc_tax AS ss_ss_net_paid_inc_tax, 
ss.ss_net_profit AS ss_ss_net_profit, 
ss_sold_date.d_date_sk AS ss_sold_date_d_date_sk, 
ss_sold_date.d_date_id AS ss_sold_date_d_date_id, 
ss_sold_date.d_date AS ss_sold_date_d_date, 
ss_sold_date.d_month_seq AS ss_sold_date_d_month_seq, 
ss_sold_date.d_week_seq AS ss_sold_date_d_week_seq, 
ss_sold_date.d_quarter_seq AS ss_sold_date_d_quarter_seq, 
ss_sold_date.d_year AS ss_sold_date_d_year, 
ss_sold_date.d_dow AS ss_sold_date_d_dow, 
ss_sold_date.d_moy AS ss_sold_date_d_moy, 
ss_sold_date.d_dom AS ss_sold_date_d_dom, 
ss_sold_date.d_qoy AS ss_sold_date_d_qoy, 
ss_sold_date.d_fy_year AS ss_sold_date_d_fy_year, 
ss_sold_date.d_fy_quarter_seq AS ss_sold_date_d_fy_quarter_seq, 
ss_sold_date.d_fy_week_seq AS ss_sold_date_d_fy_week_seq, 
ss_sold_date.d_day_name AS ss_sold_date_d_day_name, 
ss_sold_date.d_quarter_name AS ss_sold_date_d_quarter_name, 
ss_sold_date.d_holiday AS ss_sold_date_d_holiday, 
ss_sold_date.d_weekend AS ss_sold_date_d_weekend, 
ss_sold_date.d_following_holiday AS ss_sold_date_d_following_holiday, 
ss_sold_date.d_first_dom AS ss_sold_date_d_first_dom,
ss_sold_date.d_last_dom AS ss_sold_date_d_last_dom, 
ss_sold_date.d_same_day_ly AS ss_sold_date_d_same_day_ly, 
ss_sold_date.d_same_day_lq AS ss_sold_date_d_same_day_lq, 
ss_sold_date.d_current_day AS ss_sold_date_d_current_day, 
ss_sold_date.d_current_week AS ss_sold_date_d_current_week, 
ss_sold_date.d_current_month AS ss_sold_date_d_current_month, 
ss_sold_date.d_current_quarter AS ss_sold_date_d_current_quarter, 
ss_sold_date.d_current_year AS ss_sold_date_d_current_year, 
ss_sold_time.t_time_sk AS ss_sold_time_t_time_sk, 
ss_sold_time.t_time_id AS ss_sold_time_t_time_id, 
ss_sold_time.t_time AS ss_sold_time_t_time, 
ss_sold_time.t_hour AS ss_sold_time_t_hour, 
ss_sold_time.t_minute AS ss_sold_time_t_minute, 
ss_sold_time.t_second AS ss_sold_time_t_second, 
ss_sold_time.t_am_pm AS ss_sold_time_t_am_pm, 
ss_sold_time.t_shift AS ss_sold_time_t_shift, 
ss_sold_time.t_sub_shift AS ss_sold_time_t_sub_shift, 
ss_sold_time.t_meal_time AS ss_sold_time_t_meal_time, 
ss_item.i_item_sk AS ss_item_i_item_sk, 
ss_item.i_item_id AS ss_item_i_item_id, 
ss_item.i_rec_start_date AS ss_item_i_rec_start_date, 
ss_item.i_rec_end_date AS ss_item_i_rec_end_date, 
ss_item.i_item_desc AS ss_item_i_item_desc, 
ss_item.i_current_price AS ss_item_i_current_price, 
ss_item.i_wholesale_cost AS ss_item_i_wholesale_cost, 
ss_item.i_brand_id AS ss_item_i_brand_id, 
ss_item.i_brand AS ss_item_i_brand, 
ss_item.i_class_id AS ss_item_i_class_id, 
ss_item.i_class AS ss_item_i_class, 
ss_item.i_category_id AS ss_item_i_category_id, 
ss_item.i_category AS ss_item_i_category, 
ss_item.i_manufact_id AS ss_item_i_manufact_id, 
ss_item.i_manufact AS ss_item_i_manufact, 
ss_item.i_size AS ss_item_i_size, 
ss_item.i_formulation AS ss_item_i_formulation, 
ss_item.i_color AS ss_item_i_color, 
ss_item.i_units AS ss_item_i_units, 
ss_item.i_container AS ss_item_i_container, 
ss_item.i_manager_id AS ss_item_i_manager_id, 
ss_item.i_product_name AS ss_item_i_product_name, 
ss_customer.c_customer_sk AS ss_customer_c_customer_sk, 
ss_customer.c_customer_id AS ss_customer_c_customer_id, 
ss_customer.c_current_cdemo_sk AS ss_customer_c_current_cdemo_sk, 
ss_customer.c_current_hdemo_sk AS ss_customer_c_current_hdemo_sk, 
ss_customer.c_current_addr_sk AS ss_customer_c_current_addr_sk, 
ss_customer.c_first_shipto_date_sk AS ss_customer_c_first_shipto_date_sk, 
ss_customer.c_first_sales_date_sk AS ss_customer_c_first_sales_date_sk, 
ss_customer.c_salutation AS ss_customer_c_salutation, 
ss_customer.c_first_name AS ss_customer_c_first_name, 
ss_customer.c_last_name AS ss_customer_c_last_name, 
ss_customer.c_preferred_cust_flag AS ss_customer_c_preferred_cust_flag, 
ss_customer.c_birth_day AS ss_customer_c_birth_day, 
ss_customer.c_birth_month AS ss_customer_c_birth_month, 
ss_customer.c_birth_year AS ss_customer_c_birth_year, 
ss_customer.c_birth_country AS ss_customer_c_birth_country, 
ss_customer.c_login AS ss_customer_c_login, 
ss_customer.c_email_address AS ss_customer_c_email_address, 
ss_customer.c_last_review_date AS ss_customer_c_last_review_date, 
ss_cdemo.cd_demo_sk AS ss_cdemo_cd_demo_sk, 
ss_cdemo.cd_gender AS ss_cdemo_cd_gender, 
ss_cdemo.cd_marital_status AS ss_cdemo_cd_marital_status, 
ss_cdemo.cd_education_status AS ss_cdemo_cd_education_status, 
ss_cdemo.cd_purchase_estimate AS ss_cdemo_cd_purchase_estimate, 
ss_cdemo.cd_credit_rating AS ss_cdemo_cd_credit_rating, 
ss_cdemo.cd_dep_count AS ss_cdemo_cd_dep_count, 
ss_cdemo.cd_dep_employed_count AS ss_cdemo_cd_dep_employed_count, 
ss_cdemo.cd_dep_college_count AS ss_cdemo_cd_dep_college_count, 
ss_hdemo.hd_demo_sk AS ss_hdemo_hd_demo_sk, 
ss_hdemo.hd_income_band_sk AS ss_hdemo_hd_income_band_sk, 
ss_hdemo.hd_buy_potential AS ss_hdemo_hd_buy_potential, 
ss_hdemo.hd_dep_count AS ss_hdemo_hd_dep_count, 
ss_hdemo.hd_vehicle_count AS ss_hdemo_hd_vehicle_count, 
ss_addr.ca_address_sk AS ss_addr_ca_address_sk, 
ss_addr.ca_address_id AS ss_addr_ca_address_id, 
ss_addr.ca_street_number AS ss_addr_ca_street_number, 
ss_addr.ca_street_name AS ss_addr_ca_street_name, 
ss_addr.ca_street_type AS ss_addr_ca_street_type, 
ss_addr.ca_suite_number AS ss_addr_ca_suite_number, 
ss_addr.ca_city AS ss_addr_ca_city, 
ss_addr.ca_county AS ss_addr_ca_county, 
ss_addr.ca_state AS ss_addr_ca_state, 
ss_addr.ca_zip AS ss_addr_ca_zip, 
ss_addr.ca_country AS ss_addr_ca_country, 
ss_addr.ca_gmt_offset AS ss_addr_ca_gmt_offset, 
ss_addr.ca_location_type AS ss_addr_ca_location_type, 
ss_promo.p_promo_sk AS ss_promo_p_promo_sk, 
ss_promo.p_promo_id AS ss_promo_p_promo_id, 
ss_promo.p_start_date_sk AS ss_promo_p_start_date_sk, 
ss_promo.p_end_date_sk AS ss_promo_p_end_date_sk, 
ss_promo.p_item_sk AS ss_promo_p_item_sk, 
ss_promo.p_cost AS ss_promo_p_cost, 
ss_promo.p_response_target AS ss_promo_p_response_target, 
ss_promo.p_promo_name AS ss_promo_p_promo_name, 
ss_promo.p_channel_dmail AS ss_promo_p_channel_dmail, 
ss_promo.p_channel_email AS ss_promo_p_channel_email, 
ss_promo.p_channel_catalog AS ss_promo_p_channel_catalog, 
ss_promo.p_channel_tv AS ss_promo_p_channel_tv, 
ss_promo.p_channel_radio AS ss_promo_p_channel_radio, 
ss_promo.p_channel_press AS ss_promo_p_channel_press, 
ss_promo.p_channel_event AS ss_promo_p_channel_event, 
ss_promo.p_channel_demo AS ss_promo_p_channel_demo, 
ss_promo.p_channel_details AS ss_promo_p_channel_details, 
ss_promo.p_purpose AS ss_promo_p_purpose, 
ss_promo.p_discount_active AS ss_promo_p_discount_active, 
ss_store.s_store_sk AS ss_store_s_store_sk, 
ss_store.s_store_id AS ss_store_s_store_id, 
ss_store.s_rec_start_date AS ss_store_s_rec_start_date, 
ss_store.s_rec_end_date AS ss_store_s_rec_end_date, 
ss_store.s_closed_date_sk AS ss_store_s_closed_date_sk, 
ss_store.s_store_name AS ss_store_s_store_name, 
ss_store.s_number_employees AS ss_store_s_number_employees, 
ss_store.s_floor_space AS ss_store_s_floor_space, ss_store.s_hours AS ss_store_s_hours, ss_store.s_manager AS ss_store_s_manager, 
ss_store.s_market_id AS ss_store_s_market_id, ss_store.s_geography_class AS ss_store_s_geography_class, 
ss_store.s_market_desc AS ss_store_s_market_desc, ss_store.s_market_manager AS ss_store_s_market_manager, 
ss_store.s_division_id AS ss_store_s_division_id, ss_store.s_division_name AS ss_store_s_division_name, 
ss_store.s_company_id AS ss_store_s_company_id, ss_store.s_company_name AS ss_store_s_company_name, 
ss_store.s_street_number AS ss_store_s_street_number, ss_store.s_street_name AS ss_store_s_street_name, 
ss_store.s_street_type AS ss_store_s_street_type, ss_store.s_suite_number AS ss_store_s_suite_number, 
ss_store.s_city AS ss_store_s_city, ss_store.s_county AS ss_store_s_county, ss_store.s_state AS ss_store_s_state, ss_store.s_zip AS ss_store_s_zip, 
ss_store.s_country AS ss_store_s_country, ss_store.s_gmt_offset AS ss_store_s_gmt_offset, ss_store.s_tax_precentage AS ss_store_s_tax_precentage, 
ss_c_current_addr.ca_address_sk AS ss_c_current_addr_ca_address_sk, ss_c_current_addr.ca_address_id AS ss_c_current_addr_ca_address_id, 
ss_c_current_addr.ca_street_number AS ss_c_current_addr_ca_street_number, ss_c_current_addr.ca_street_name AS ss_c_current_addr_ca_street_name, 
ss_c_current_addr.ca_street_type AS ss_c_current_addr_ca_street_type, ss_c_current_addr.ca_suite_number AS ss_c_current_addr_ca_suite_number, 
ss_c_current_addr.ca_city AS ss_c_current_addr_ca_city, ss_c_current_addr.ca_county AS ss_c_current_addr_ca_county, 
ss_c_current_addr.ca_state AS ss_c_current_addr_ca_state, ss_c_current_addr.ca_zip AS ss_c_current_addr_ca_zip, 
ss_c_current_addr.ca_country AS ss_c_current_addr_ca_country, ss_c_current_addr.ca_gmt_offset AS ss_c_current_addr_ca_gmt_offset, 
ss_c_current_addr.ca_location_type AS ss_c_current_addr_ca_location_type

FROM store_sales as ss,
date_dim as ss_sold_date,
time_dim as ss_sold_time,
item as ss_item,
customer as ss_customer,
customer_demographics as ss_cdemo,
household_demographics as ss_hdemo,
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
AND ss_customer.c_current_addr_sk = ss_c_current_addr.ca_address_sk

