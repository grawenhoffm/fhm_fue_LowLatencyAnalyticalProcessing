
select sum (ss_ss_quantity)
 from bigTable
 where ss_sold_date_d_year = 1998
 and
 (
  (
   ss_cdemo_cd_marital_status = 'M'
   and
   ss_cdemo_cd_education_status = '4 yr Degree'
   and
   ss_ss_sales_price between 100.00 and 150.00
   )
 or
  (
   ss_cdemo_cd_marital_status = 'D'
   and
   ss_cdemo_cd_education_status = 'Primary'
   and
   ss_ss_sales_price between 50.00 and 100.00
  )
 or
 (
   ss_cdemo_cd_marital_status = 'U'
   and
   ss_cdemo_cd_education_status = 'Advanced Degree'
   and
   ss_ss_sales_price between 150.00 and 200.00
 )
 )
 and
 (
  (
  ss_addr_ca_country = 'United States'
  and
  ss_addr_ca_state in ('KY', 'GA', 'NM')
  and ss_ss_net_profit between 0 and 2000
  )
 or
  (
  ss_addr_ca_country = 'United States'
  and
  ss_addr_ca_state in ('MT', 'OR', 'IN')
  and ss_ss_net_profit between 150 and 3000
  )
 or
  (
  ss_addr_ca_country = 'United States'
  and
  ss_addr_ca_state in ('WI', 'MO', 'WV')
  and ss_ss_net_profit between 50 and 25000
  )
 )
;
