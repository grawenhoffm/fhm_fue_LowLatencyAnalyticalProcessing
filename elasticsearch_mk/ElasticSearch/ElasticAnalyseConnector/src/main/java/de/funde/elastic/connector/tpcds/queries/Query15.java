package de.funde.elastic.connector.tpcds.queries;

import java.util.Optional;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.funde.elastic.config.StopWatch;

/**
 * select  ca_zip
       ,sum(cs_sales_price)
 from catalog_sales
     ,customer
     ,customer_address
     ,date_dim
 where cs_bill_customer_sk = c_customer_sk
 	and c_current_addr_sk = ca_address_sk
 	and ( substr(ca_zip,1,5) in ('85669', '86197','88274','83405','86475',
                                   '85392', '85460', '80348', '81792')
 	      or ca_state in ('CA','WA','GA')
 	      or cs_sales_price > 500)
 	and cs_sold_date_sk = d_date_sk
 	and d_qoy = 2 and d_year = 2000
 group by ca_zip
 order by ca_zip
 limit 100;
 * @author Marco
 *
 */
public class Query15 implements IQuery {

	private final RestHighLevelClient client;
	private static final Logger LOG = LoggerFactory.getLogger(Query15.class);


	public Query15(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public Optional<StopWatch> run(String indexname, int counter) {


		return Optional.empty();
	}

}
