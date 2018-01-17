
select  ss_item_i_brand_id brand_id, ss_item_i_brand brand,
 	sum(ss_ss_ext_sales_price) ext_price
 from default.bigTable
 where ss_item_i_manager_id=36
 	and ss_sold_date_d_moy=12
 	and ss_sold_date_d_year=2001
 group by ss_item_i_brand, ss_item_i_brand_id
 order by ext_price desc, ss_item_i_brand_id
limit 100 ;
