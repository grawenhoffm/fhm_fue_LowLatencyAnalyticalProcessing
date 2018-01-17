
select  count(*) as c
from default.bigTable
where ss_sold_time_t_hour = 8
    and ss_sold_time_t_minute >= 30
    and ss_hdemo_hd_dep_count = 5
    and ss_store_s_store_name = 'ese'
order by c
limit 100;
