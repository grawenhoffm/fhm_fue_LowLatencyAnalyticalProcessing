package de.mk.elasticsearch.tableobjects;

public class Column {

	private String columnName;
	private String type;
	private boolean primary;
	private boolean nullable;

	public boolean isPrimary() {
		return this.primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	public String getType() {
		return this.type.trim();
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColumnName() {
		return this.columnName.trim();
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public boolean isNullable() {
		return this.nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public String toString() {
		return "Spalte: " +  this.columnName + " - " + this.type + " - Nullable: " + this.nullable + " - Primary: " + this.primary;
	}
}
