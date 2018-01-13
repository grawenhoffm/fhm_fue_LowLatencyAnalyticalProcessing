
select ss_item_i_item_id
      ,ss_item_i_item_desc
      ,ss_item_i_category
      ,ss_item_i_class
      ,ss_item_i_current_price
      ,sum(ss_ss_ext_sales_price) as itemrevenue
      ,sum(ss_ss_ext_sales_price)*100 as revenueratio
from
	bigTable
where
	ss_item_i_category in ('Jewelry', 'Sports', 'Books')
  	and ss_sold_date_d_date between cast('2001-01-12' as date)
				and (cast('2001-01-12' as date) + interval '30' day)
group by
	ss_item_i_item_id
        ,ss_item_i_item_desc
        ,ss_item_i_category
        ,ss_item_i_class
        ,ss_item_i_current_price
order by
	ss_item_i_category
        ,ss_item_i_class
        ,ss_item_i_item_id
        ,ss_item_i_item_desc
        ,revenueratio;
