select  ss_item_i_brand_id ss_item_brand_id, ss_item_i_brand brand, ss_item_i_manufact_id, ss_item_i_manufact,
 	sum(ss_ss_ext_sales_price) ext_price
 from default.bigTable
   where ss_item_i_manager_id=7
   and ss_sold_date_d_moy=11
   and ss_sold_date_d_year=1999
   and substr(ss_addr_ca_zip,0,5) <> substr(ss_store_s_zip,0,5)
 group by ss_item_i_brand
      ,ss_item_i_brand_id
      ,ss_item_i_manufact_id
      ,ss_item_i_manufact
 order by ext_price desc
         ,ss_item_i_brand
         ,ss_item_i_brand_id
         ,ss_item_i_manufact_id
         ,ss_item_i_manufact
limit 100 ;
