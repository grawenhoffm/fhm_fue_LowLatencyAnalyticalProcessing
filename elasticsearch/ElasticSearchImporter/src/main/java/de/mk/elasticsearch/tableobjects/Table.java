package de.mk.elasticsearch.tableobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

	private String name;
	private final List<Column> columns = new ArrayList<>();

	public Table(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name.trim();
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Column> getColumns() {
		return this.columns;
	}
	public void addColumn(Column column) {
		this.columns.add(column);
	}

	public List<String> getPrimaryKeyColumnNames() {
		return this.columns.stream().filter(x -> x.isPrimary()).map(Column::getColumnName).collect(Collectors.toList());
	}

	public void setPrimaryKeys(List<String> pkColumnNames) {
		this.columns.stream().filter(x -> pkColumnNames.contains(x.getColumnName()))
			.forEach(x -> {
				x.setPrimary(true);
				}
			);
	}

	@Override
	public String toString() {
		String returnString = "Tabelle: " +  this.name + "\n";
		for (final Column c : this.columns) {
			returnString += "		- " + c.toString() + "\n";
		}
		return returnString;
	}
}
