 select  ss_item_i_item_id,
         avg(ss_ss_quantity) agg1,
         avg(ss_ss_list_price) agg2,
         avg(ss_ss_coupon_amt) agg3,
         avg(ss_ss_sales_price) agg4
  from default.orcbigtable
  where
        ss_cdemo_cd_gender = 'F' and
        ss_cdemo_cd_marital_status = 'W' and
        ss_cdemo_cd_education_status = 'Primary' and
        (ss_promo_p_channel_email = 'N' or ss_promo_p_channel_event = 'N') and
        ss_sold_date_d_year = 1998
  group by ss_item_i_item_id
  order by ss_item_i_item_id
  limit 100;
