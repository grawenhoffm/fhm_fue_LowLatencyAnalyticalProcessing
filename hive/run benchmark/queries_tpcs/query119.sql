select  ss_item_i_item_id,
        ss_store_s_state,
        avg(ss_ss_quantity) agg1,
        avg(ss_ss_list_price) agg2,
        avg(ss_ss_coupon_amt) agg3,
        avg(ss_ss_sales_price) agg4
 from bigTable
 where ss_cdemo_cd_gender = 'F' and
       ss_cdemo_cd_marital_status = 'W' and
       ss_cdemo_cd_education_status = 'Primary' and
       ss_sold_date_d_year = 1998 and
       ss_store_s_state in ('TN','TN', 'TN', 'TN', 'TN', 'TN')
 group by ss_item_i_item_id, ss_store_s_state
 order by ss_item_i_item_id
         ,ss_store_s_state
 limit 100;
