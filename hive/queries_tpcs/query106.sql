
select  dt.d_year as y, i.i_category_id,i.i_category,sum(ss.ss_ext_sales_price) as s
from date_dim as dt
join store_sales as ss on dt.d_date_sk = ss.ss_sold_date_sk
join item as i on ss.ss_item_sk = i.i_item_sk
where
	i.i_manager_id = 1
	 	and dt.d_moy=12
	 	and dt.d_year=1998
	 GROUP BY
	 		dt.d_year
	 		,i_category_id
	 		,i_category
	 order by
	 		s desc
	 		,dt.d_year
	 		,i.i_category_id
	 		,i.i_category
	limit 100 ;
