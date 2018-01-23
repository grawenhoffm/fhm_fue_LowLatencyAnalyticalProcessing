select  ss_addr_ca_state as state, count(*) cnt
 from default.orcbigtable
   where ss_sold_date_d_year = 2000
     and ss_sold_date_d_moy = 2
 group by ss_addr_ca_state
 order by cnt
 limit 100;
