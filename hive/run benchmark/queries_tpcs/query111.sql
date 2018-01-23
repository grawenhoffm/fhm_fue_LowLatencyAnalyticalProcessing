select
     sum(ss_net_profit) as total_sum
    ,s_state
    ,s_county
from store_sales
  join date_dim d1 on d1.d_date_sk = store_sales.ss_sold_date_sk
  join store on store.s_store_sk  = store_sales.ss_store_sk
where d1.d_month_seq between 1212 and 1212+11
  and s_state in
              (select store.s_state as s_state
                     from store_sales, store, date_dim
                       join date_dim on date_dim.d_date_sk = store_sales.ss_sold_date_sk
                       join store on store.s_store_sk  = store_sales.ss_store_sk
                     where  d_month_seq between 1212 and 1212+11
                       and d_date_sk = ss_sold_date_sk
                       and s_store_sk  = ss_store_sk
                     group by s_state
                   ) tmp1
group by s_state,s_county
limit 100;
