
select
    sum(ss_ss_net_profit) as total_sum
   ,ss_store_s_state
   ,ss_store_s_county
 from
    bigTable
 where
    ss_sold_date_d_month_seq between 1212 and 1212+11
group by ss_store_s_state, ss_store_s_county
 limit 100;
