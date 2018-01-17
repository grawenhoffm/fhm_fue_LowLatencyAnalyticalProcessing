
select  ss_sold_date_d_year
 	,ss_item_i_brand_id brand_id
 	,ss_item_i_brand brand
 	,sum(ss_ss_ext_sales_price) ext_price
 from default.bigTable
 where ss_item_i_manager_id = 1
    and ss_sold_date_d_moy=12
    and ss_sold_date_d_year=1998
 group by ss_sold_date_d_year
 	,ss_item_i_brand
 	,ss_item_i_brand_id
 order by ss_sold_date_d_year
 	,ext_price desc
 	,brand_id
limit 100 ;
