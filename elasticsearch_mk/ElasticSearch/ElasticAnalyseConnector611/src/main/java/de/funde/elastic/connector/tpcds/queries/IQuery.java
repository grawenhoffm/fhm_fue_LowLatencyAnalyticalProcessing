package de.funde.elastic.connector.tpcds.queries;

import java.util.Optional;

import de.funde.elastic.config.StopWatch;

public interface IQuery {

	public Optional<StopWatch> run(String indexname, int counter);
}
