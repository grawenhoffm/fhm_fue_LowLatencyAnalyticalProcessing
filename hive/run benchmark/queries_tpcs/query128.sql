select  ss_sold_date_d_year as y
       ,ss_item_i_brand_id brand_id
       ,ss_item_i_brand brand
       ,sum(ss_ss_ext_sales_price) sum_agg
 from  default.orcbigtable
 where ss_item_i_manufact_id = 436
   and ss_sold_date_d_moy  =12
 group by ss_sold_date_d_year
      ,ss_item_i_brand
      ,ss_item_i_brand_id
 order by y
         ,sum_agg desc
         ,brand_id
 limit 100;
