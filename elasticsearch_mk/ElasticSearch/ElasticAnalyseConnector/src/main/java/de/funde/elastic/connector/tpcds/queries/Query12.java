package de.funde.elastic.connector.tpcds.queries;

import java.util.Optional;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;

/**
 * select  i_item_id
      ,i_item_desc
      ,i_category
      ,i_class
      ,i_current_price
      ,sum(ws_ext_sales_price) as itemrevenue
      ,sum(ws_ext_sales_price)*100/sum(sum(ws_ext_sales_price)) over
          (partition by i_class) as revenueratio
from
	web_sales
    	,item
    	,date_dim
where
	ws_item_sk = i_item_sk
  	and i_category in ('Jewelry', 'Sports', 'Books')
  	and ws_sold_date_sk = d_date_sk
	and d_date between cast('2001-01-12' as date)
				and (cast('2001-01-12' as date) + interval '30' day)
group by
	i_item_id
        ,i_item_desc
        ,i_category
        ,i_class
        ,i_current_price
order by
	i_category
        ,i_class
        ,i_item_id
        ,i_item_desc
        ,revenueratio
limit 100;
 * @author Marco
 *
 */
public class Query12 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query12.class);


	public Query12(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {


		return Optional.empty();
	}

}
