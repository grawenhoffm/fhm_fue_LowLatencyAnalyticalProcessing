package de.funde.elastic.connector;

import java.io.IOException;

public interface IndexCreator {

	/**
	 * Löscht alle Daten aus dem Index und Legt diesen mit dem Mapping neu an
	 * Draaufhin wird dieser mit Test-Daten befüllt
	 * @throws IOException
	 */
	public void deleteAndCreateIndex() throws IOException ;
}
