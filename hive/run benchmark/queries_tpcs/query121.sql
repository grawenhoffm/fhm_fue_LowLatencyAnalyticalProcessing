select  ss_store_s_store_name, ss_store_s_store_id,
        sum(case when (ss_sold_date_d_day_name='Sunday') then ss_ss_sales_price else null end) sun_sales,
        sum(case when (ss_sold_date_d_day_name='Monday') then ss_ss_sales_price else null end) mon_sales,
        sum(case when (ss_sold_date_d_day_name='Tuesday') then ss_ss_sales_price else  null end) tue_sales,
        sum(case when (ss_sold_date_d_day_name='Wednesday') then ss_ss_sales_price else null end) wed_sales,
        sum(case when (ss_sold_date_d_day_name='Thursday') then ss_ss_sales_price else null end) thu_sales,
        sum(case when (ss_sold_date_d_day_name='Friday') then ss_ss_sales_price else null end) fri_sales,
        sum(case when (ss_sold_date_d_day_name='Saturday') then ss_ss_sales_price else null end) sat_sales
 from default.bigTable
 where ss_store_s_gmt_offset = -6 and
       ss_sold_date_d_year = 1998
 group by ss_store_s_store_name, ss_store_s_store_id
 order by ss_store_s_store_name, ss_store_s_store_id, sun_sales,mon_sales, tue_sales, wed_sales, thu_sales, fri_sales, sat_sales
 limit 100;
