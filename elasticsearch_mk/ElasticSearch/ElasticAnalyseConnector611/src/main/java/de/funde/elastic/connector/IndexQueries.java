package de.funde.elastic.connector;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IndexQueries {

	default void runAll() {


		try {
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.getRequest();
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.searchRequestWithSort();
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.searchRequestWithParentFilterText();
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.searchRequestWithParentIDFilter();
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.searchRequestWithParentIDFilterAndAggregation("3");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.searchRequestWithParentIDFilterAndAggregation("4");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			this.searchRequestWithParentIDFilterAndAggregationAndGrouping("4");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
			LogHolder.LOG.debug("##############################################");
		} catch (final IOException e) {
			LogHolder.LOG.error(e.getMessage(), e);
		}

	}


	void getRequest() throws IOException;

	void searchRequestWithSort();

	void searchRequestWithParentFilterText();

	void searchRequestWithParentIDFilter();

	void searchRequestWithParentIDFilterAndAggregation(String string);

	void searchRequestWithParentIDFilterAndAggregationAndGrouping(String string);

}

final class LogHolder { // not public
    static final Logger LOG = LoggerFactory.getLogger(IndexQueries.class);
}
