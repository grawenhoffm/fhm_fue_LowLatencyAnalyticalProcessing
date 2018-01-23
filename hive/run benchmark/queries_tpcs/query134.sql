
	select  ss_sold_date_d_year as y, ss_item_i_category_id, ss_item_i_category, sum(ss_ss_ext_sales_price) as s
	from default.orcbigtable
	where
		ss_item_i_manager_id = 1
		 	and ss_sold_date_d_moy=12
		 	and ss_sold_date_d_year=1998
		 GROUP BY
		 		ss_sold_date_d_year
		 		,ss_sold_date_d_year
		 		,ss_item_i_category
				,ss_item_i_category_id
		 order by
		 		s desc
		 		,ss_sold_date_d_year
		 		,ss_sold_date_d_year
		 		,ss_item_i_category
		limit 100 ;
