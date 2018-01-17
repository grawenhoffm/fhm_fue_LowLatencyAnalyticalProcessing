select avg(ss_ss_quantity)
       ,avg(ss_ss_ext_sales_price)
       ,avg(ss_ss_ext_wholesale_cost)
       ,sum(ss_ss_ext_wholesale_cost)
 from default.bigTable
 where ss_sold_date_d_year = 2001
 and((ss_cdemo_cd_marital_status = 'D'
  and ss_cdemo_cd_education_status = '2 yr Degree'
  and ss_ss_sales_price between 100.00 and 150.00
  and ss_hdemo_hd_dep_count = 3
     )or
     (ss_cdemo_cd_marital_status = 'S'
  and ss_cdemo_cd_education_status = 'Secondary'
  and ss_ss_sales_price between 50.00 and 100.00
  and ss_hdemo_hd_dep_count = 1
     ) or
     (ss_cdemo_cd_marital_status = 'W'
  and ss_cdemo_cd_education_status = 'Advanced Degree'
  and ss_ss_sales_price between 150.00 and 200.00
  and ss_hdemo_hd_dep_count = 1
     ))
 and((ss_addr_ca_country = 'United States'
  and ss_addr_ca_state in ('CO', 'IL', 'MN')
  and ss_ss_net_profit between 100 and 200
     ) or
     (ss_addr_ca_country = 'United States'
  and ss_addr_ca_state in ('OH', 'MT', 'NM')
  and ss_ss_net_profit between 150 and 300
     ) or
     (ss_addr_ca_country = 'United States'
  and ss_addr_ca_state in ('TX', 'MO', 'MI')
  and ss_ss_net_profit between 50 and 250
     ))
;
