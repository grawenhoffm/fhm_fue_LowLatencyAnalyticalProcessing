select count(*) as c
from store_sales
join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
join store on store_sales.ss_store_sk = store.s_store_sk
where time_dim.t_hour = 8
  and time_dim.t_minute >= 30
  and household_demographics.hd_dep_count = 5
  and store.s_store_name = 'ese'
order by c
limit 100;
