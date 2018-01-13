--autor Christian Latus/ Daniel Reider

create table if not exists s_catalog_page
(
    cpag_catalog_number         int               ,
    cpag_catalog_page_number    int               ,
    cpag_department             char(20)                      ,
    cpag_id                     char(16)                      ,
    cpag_start_date             char(10)                      ,
    cpag_end_date               char(10)                      ,
    cpag_description            varchar(100)                  ,
    cpag_type                   varchar(100)
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY "|"
LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_zip_to_gmt
-- ============================================================
create table if not exists s_zip_to_gmt
(
    zipg_zip                    char(5)               ,
    zipg_gmt_offset             int
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_purchase_lineitem
-- ============================================================
create table if not exists s_purchase_lineitem
(
    plin_purchase_id            int               ,
    plin_line_number            int               ,
    plin_item_id                char(16)                      ,
    plin_promotion_id           char(16)                      ,
    plin_quantity               int                       ,
    plin_sale_price             decimal(7,2)                  ,
    plin_coupon_amt             decimal(7,2)                  ,
    plin_comment                varchar(100)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_customer
-- ============================================================
create table if not exists s_customer
(
    cust_customer_id            char(16)              ,
    cust_salutation             char(10)                      ,
    cust_last_name              char(20)                      ,
    cust_first_name             char(20)                      ,
    cust_preffered_flag         char(1)                       ,
    cust_birth_date             char(10)                      ,
    cust_birth_country          char(20)                      ,
    cust_login_id               char(13)                      ,
    cust_email_address          char(50)                      ,
    cust_last_login_chg_date    char(10)                      ,
    cust_first_shipto_date      char(10)                      ,
    cust_first_purchase_date    char(10)                      ,
    cust_last_review_date       char(10)                      ,
    cust_primary_machine_id     char(15)                      ,
    cust_secondary_machine_id   char(15)                      ,
    cust_street_number          smallint                      ,
    cust_suite_number           char(10)                      ,
    cust_street_name1           char(30)                      ,
    cust_street_name2           char(30)                      ,
    cust_street_type            char(15)                      ,
    cust_city                   char(60)                      ,
    cust_zip                    char(10)                      ,
    cust_county                 char(30)                      ,
    cust_state                  char(2)                       ,
    cust_country                char(20)                      ,
    cust_loc_type               char(20)                      ,
    cust_gender                 char(1)                       ,
    cust_marital_status         char(1)                       ,
    cust_educ_status            char(20)                      ,
    cust_credit_rating          char(10)                      ,
    cust_purch_est              decimal(7,2)                  ,
    cust_buy_potential          char(15)                      ,
    cust_depend_cnt             smallint                      ,
    cust_depend_emp_cnt         smallint                      ,
    cust_depend_college_cnt     smallint                      ,
    cust_vehicle_cnt            smallint                      ,
    cust_annual_income          decimal(9,2)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_customer_address
-- ============================================================
create table if not exists s_customer_address
(
    cadr_address_id             char(16)              ,
    cadr_street_number          int                       ,
    cadr_street_name1           char(25)                      ,
    cadr_street_name2           char(25)                      ,
    cadr_street_type            char(15)                      ,
    cadr_suitnumber             char(10)                      ,
    cadr_city                   char(60)                      ,
    cadr_county                 char(30)                      ,
    cadr_state                  char(2)                       ,
    cadr_zip                    char(10)                      ,
    cadr_country                char(20)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_purchase
-- ============================================================
create table if not exists s_purchase
(
    purc_purchase_id            int               ,
    purc_store_id               char(16)                      ,
    purc_customer_id            char(16)                      ,
    purc_purchase_date          char(10)                      ,
    purc_purchase_time          int                       ,
    purc_register_id            int                       ,
    purc_clerk_id               int                       ,
    purc_comment                char(100)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_catalog_order
-- ============================================================
create table if not exists s_catalog_order
(
    cord_order_id               int               ,
    cord_bill_customer_id       char(16)                      ,
    cord_ship_customer_id       char(16)                      ,
    cord_order_date             char(10)                      ,
    cord_order_time             int                       ,
    cord_ship_mode_id           char(16)                      ,
    cord_call_center_id         char(16)                      ,
    cord_order_comments         varchar(100)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_web_order
-- ============================================================
create table if not exists s_web_order
(
    word_order_id               int               ,
    word_bill_customer_id       char(16)                      ,
    word_ship_customer_id       char(16)                      ,
    word_order_date             char(10)                      ,
    word_order_time             int                       ,
    word_ship_mode_id           char(16)                      ,
    word_web_site_id            char(16)                      ,
    word_order_comments         char(100)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_item
-- ============================================================
create table if not exists s_item
(
    item_item_id                char(16)              ,
    item_item_description       char(200)                     ,
    item_list_price             decimal(7,2)                  ,
    item_wholesale_cost         decimal(7,2)                  ,
    item_size                   char(20)                      ,
    item_formulation            char(20)                      ,
    item_color                  char(20)                      ,
    item_units                  char(10)                      ,
    item_container              char(10)                      ,
    item_manager_id             int
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_catalog_order_lineitem
-- ============================================================
create table if not exists s_catalog_order_lineitem
(
    clin_order_id               int               ,
    clin_line_number            int               ,
    clin_item_id                char(16)                      ,
    clin_promotion_id           char(16)                      ,
    clin_quantity               int                       ,
    clin_sales_price            decimal(7,2)                  ,
    clin_coupon_amt             decimal(7,2)                  ,
    clin_warehouse_id           char(16)                      ,
    clin_ship_date              char(10)                      ,
    clin_catalog_number         int                       ,
    clin_catalog_page_number    int                       ,
    clin_ship_cost              decimal(7,2)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_web_order_lineitem
-- ============================================================
create table if not exists s_web_order_lineitem
(
    wlin_order_id               int               ,
    wlin_line_number            int               ,
    wlin_item_id                char(16)                      ,
    wlin_promotion_id           char(16)                      ,
    wlin_quantity               int                       ,
    wlin_sales_price            decimal(7,2)                  ,
    wlin_coupon_amt             decimal(7,2)                  ,
    wlin_warehouse_id           char(16)                      ,
    wlin_ship_date              char(10)                      ,
    wlin_ship_cost              decimal(7,2)                  ,
    wlin_web_page_id            char(16)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_store
-- ============================================================
create table if not exists s_store
(
    stor_store_id               char(16)              ,
    stor_closed_date            char(10)                      ,
    stor_name                   char(50)                      ,
    stor_employees              int                       ,
    stor_floor_space            int                       ,
    stor_hours                  char(20)                      ,
    stor_store_manager          char(40)                      ,
    stor_market_id              int                       ,
    stor_geography_class        char(100)                     ,
    stor_market_manager         char(40)                      ,
    stor_tax_percentage         decimal(5,2)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_call_center
-- ============================================================
create table if not exists s_call_center
(
    call_center_id              char(16)              ,
    call_open_date              char(10)                      ,
    call_closed_date            char(10)                      ,
    call_center_name            char(50)                      ,
    call_center_class           char(50)                      ,
    call_center_employees       int                       ,
    call_center_sq_ft           int                       ,
    call_center_hours           char(20)                      ,
    call_center_manager         char(40)                      ,
    call_center_tax_percentage  decimal(7,2)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_web_site
-- ============================================================
create table if not exists s_web_site
(
    wsit_web_site_id            char(16)              ,
    wsit_open_date              char(10)                      ,
    wsit_closed_date            char(10)                      ,
    wsit_site_name              char(50)                      ,
    wsit_site_class             char(50)                      ,
    wsit_site_manager           char(40)                      ,
    wsit_tax_percentage         decimal(5,2)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_warehouse
-- ============================================================
create table if not exists s_warehouse
(
    wrhs_warehouse_id           char(16)              ,
    wrhs_warehouse_desc         char(200)                     ,
    wrhs_warehouse_sq_ft        int
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_web_page
-- ============================================================
create table if not exists s_web_page
(
    wpag_web_page_id            char(16)              ,
    wpag_create_date            char(10)                      ,
    wpag_access_date            char(10)                      ,
    wpag_autogen_flag           char(1)                       ,
    wpag_url                    char(100)                     ,
    wpag_type                   char(50)                      ,
    wpag_char_cnt               int                       ,
    wpag_link_cnt               int                       ,
    wpag_image_cnt              int                       ,
    wpag_max_ad_cnt             int
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_promotion
-- ============================================================
create table if not exists s_promotion
(
    prom_promotion_id           char(16)              ,
    prom_promotion_name         char(30)                      ,
    prom_start_date             char(10)                      ,
    prom_end_date               char(10)                      ,
    prom_cost                   decimal(7,2)                  ,
    prom_response_target        char(1)                       ,
    prom_channel_dmail          char(1)                       ,
    prom_channel_email          char(1)                       ,
    prom_channel_catalog        char(1)                       ,
    prom_channel_tv             char(1)                       ,
    prom_channel_radio          char(1)                       ,
    prom_channel_press          char(1)                       ,
    prom_channel_event          char(1)                       ,
    prom_channel_demo           char(1)                       ,
    prom_channel_details        char(100)                     ,
    prom_purpose                char(15)                      ,
    prom_discount_active        char(1)                       ,
    prom_discount_pct           decimal(5,2)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_store_returns
-- ============================================================
create table if not exists s_store_returns
(
    sret_store_id               char(16)                      ,
    sret_purchase_id            char(16)              ,
    sret_line_number            int               ,
    sret_item_id                char(16)              ,
    sret_customer_id            char(16)                      ,
    sret_return_date            char(10)                      ,
    sret_return_time            char(10)                      ,
    sret_ticket_number          char(20)                      ,
    sret_return_qty             int                       ,
    sret_return_amt             decimal(7,2)                  ,
    sret_return_tax             decimal(7,2)                  ,
    sret_return_fee             decimal(7,2)                  ,
    sret_return_ship_cost       decimal(7,2)                  ,
    sret_refunded_cash          decimal(7,2)                  ,
    sret_reversed_charge        decimal(7,2)                  ,
    sret_store_credit           decimal(7,2)                  ,
    sret_reason_id              char(16)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_catalog_returns
-- ============================================================
create table if not exists s_catalog_returns
(
    cret_call_center_id         char(16)                      ,
    cret_order_id               int               ,
    cret_line_number            int               ,
    cret_item_id                char(16)              ,
    cret_return_customer_id     char(16)                      ,
    cret_refund_customer_id     char(16)                      ,
    cret_return_date            char(10)                      ,
    cret_return_time            char(10)                      ,
    cret_return_qty             int                       ,
    cret_return_amt             decimal(7,2)                  ,
    cret_return_tax             decimal(7,2)                  ,
    cret_return_fee             decimal(7,2)                  ,
    cret_return_ship_cost       decimal(7,2)                  ,
    cret_refunded_cash          decimal(7,2)                  ,
    cret_reversed_charge        decimal(7,2)                  ,
    cret_merchant_credit        decimal(7,2)                  ,
    cret_reason_id              char(16)                      ,
    cret_shipmode_id            char(16)                      ,
    cret_catalog_page_id        char(16)                      ,
    cret_warehouse_id           char(16)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_web_returns
-- ============================================================
create table if not exists s_web_returns
(
    wret_web_site_id            char(16)                      ,
    wret_order_id               int               ,
    wret_line_number            int               ,
    wret_item_id                char(16)              ,
    wret_return_customer_id     char(16)                      ,
    wret_refund_customer_id     char(16)                      ,
    wret_return_date            char(10)                      ,
    wret_return_time            char(10)                      ,
    wret_return_qty             int                       ,
    wret_return_amt             decimal(7,2)                  ,
    wret_return_tax             decimal(7,2)                  ,
    wret_return_fee             decimal(7,2)                  ,
    wret_return_ship_cost       decimal(7,2)                  ,
    wret_refunded_cash          decimal(7,2)                  ,
    wret_reversed_charge        decimal(7,2)                  ,
    wret_account_credit         decimal(7,2)                  ,
    wret_reason_id              char(16)
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";

-- ============================================================
--   Table: s_inventory
-- ============================================================
create table if not exists s_inventory
(
    invn_warehouse_id           char(16)              ,
    invn_item_id                char(16)              ,
    invn_date                   char(10)              ,
    invn_qty_on_hand            int
  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY "|"
  LINES TERMINATED BY "\n";
