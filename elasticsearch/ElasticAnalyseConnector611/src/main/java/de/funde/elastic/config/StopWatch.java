package de.funde.elastic.config;

public class StopWatch {

	private final String elasticVersion;
	private final String luceneVersion;

	private final long start;
	private long beforeExecution;
	private long afterExecution;
	private long afterResultPrinting;
	private final String query;
	private long tookInMillis; // Messung durch ES
	private int totalShards;
	private int successShards;
	private int failedShards;
	private int skippedShards;
	private int numberOfReducePhases;
	private final String numberOfReplicas;
	private final String numberOfIndexShards;
	private final String indexname;
	private final int counter;


	public StopWatch(String query, String elasticVersion, String luceneVersion, String numberOfReplicas, String numberOfIndexShards, String indexname, int counter) {
		this.start = System.currentTimeMillis();
		this.query = query;
		this.elasticVersion = elasticVersion;
		this.luceneVersion = luceneVersion;
		this.numberOfIndexShards = numberOfIndexShards;
		this.numberOfReplicas = numberOfReplicas;
		this.indexname = indexname;
		this.counter = counter;
	}

	public String getQueryName() {
		return this.query;
	}
	public long getStart() {
		return this.start;
	}

	public long getAfterExecution() {
		return this.afterExecution;
	}

	public long getAfterResultPrinting() {
		return this.afterResultPrinting;
	}

	public long getBeforeExecution() {
		return this.beforeExecution;
	}

	public void beforeExecution() {
		this.beforeExecution = System.currentTimeMillis();
	}

	public void afterExecution() {
		this.afterExecution = System.currentTimeMillis();
	}

	public void afterResultPrinting() {
		this.afterResultPrinting = System.currentTimeMillis();
	}

	public String getInfo() {
		final StringBuilder sb = new StringBuilder("\n----------------------------------------------\n");
		sb.append(this.query).append("\n");
		sb.append("Durchlauf: ").append(this.counter).append("\n");
		sb.append(" ---> BuildRequest: ").append((this.beforeExecution-this.start)).append("ms\n");
		sb.append(" ---> Zeit nach der Response: ").append((this.afterExecution-this.start)).append("ms\n");
		sb.append(" ---> Zeit nach der Ergebnisausgabe: ").append((this.afterResultPrinting-this.start)).append("ms\n");
		sb.append(" ---> Zeit allein für die Ausführung: ").append((this.afterExecution-this.beforeExecution)).append("ms\n");
		sb.append(" ---> Zeit durch ES gemessen: ").append((this.tookInMillis)).append("ms\n");
		sb.append(" ---> Gesamte Shards: ").append(this.totalShards).append("\n");
		sb.append(" ---> Success Shards: ").append(this.successShards).append("\n");
		sb.append(" ---> Failed Shards: ").append(this.failedShards).append("\n");
		sb.append(" ---> Skipped Shards: ").append(this.skippedShards).append("\n");
		sb.append(" ---> Index Shards Gesamt: ").append(this.numberOfIndexShards).append("\n");
		sb.append(" ---> ReducePhasen: ").append(this.numberOfReducePhases).append("\n");
		sb.append(" ---> Replicas: ").append(this.numberOfReplicas).append("\n");
		sb.append(" ---> Indexname: ").append(this.indexname).append("\n");
		sb.append("----------------------------------------------\n");
		return sb.toString();
	}

	public String getMeasureInfo() {
		final StringBuilder sb = new StringBuilder();
		sb.append("").append(this.counter).append(";");
		sb.append("\"'").append(this.luceneVersion).append("\";"); // LuceneVersion
		sb.append("\"'").append(this.elasticVersion).append("\";"); // ElasticVersion
		sb.append("\"").append(this.indexname).append("\";"); // Indexname
		sb.append(this.numberOfReplicas).append(";"); // Replica Anzahl
		sb.append(this.numberOfIndexShards).append(";"); // gesamte Anzahl Shards des Index
		sb.append(this.totalShards).append(";"); // gesamte Anzahl Shards
		sb.append(this.successShards).append(";"); // Erfolgreiche Shards
		sb.append(this.failedShards).append(";"); // Fehlgeschlagene Shards
		sb.append(this.skippedShards).append(";"); // Übersprungene Shards
		sb.append(this.numberOfReducePhases).append(";"); // Anzahl der ReducePhasen
		sb.append("\"").append(this.query).append("\";"); // Queryname
		sb.append((this.beforeExecution-this.start)).append(";"); // BuildRequest
		sb.append((this.afterExecution-this.start)).append(";"); // Zeit nach der Response
		sb.append((this.afterResultPrinting-this.start)).append(";"); //  Zeit nach der Ergebnisausgabe
		sb.append((this.afterExecution-this.beforeExecution)).append(";"); // Zeit allein für die Ausführung
		sb.append((this.tookInMillis)).append(";"); //  Zeit durch ES gemessen
		sb.append("\n");
		return sb.toString();
	}

	public long getTookInMillis() {
		return this.tookInMillis;
	}

	public void setTookInMillis(long tookInMillis) {
		this.tookInMillis = tookInMillis;
	}

	public int getTotalShards() {
		return this.totalShards;
	}

	public void setTotalShards(int totalShards) {
		this.totalShards = totalShards;
	}

	public int getSuccessShards() {
		return this.successShards;
	}

	public void setSuccessShards(int successShards) {
		this.successShards = successShards;
	}

	public int getFailedShards() {
		return this.failedShards;
	}

	public void setFailedShards(int failedShards) {
		this.failedShards = failedShards;
	}

	public int getSkippedShards() {
		return this.skippedShards;
	}

	public void setSkippedShards(int skippedShards) {
		this.skippedShards = skippedShards;
	}

	public int getNumberOfReducePhases() {
		return this.numberOfReducePhases;
	}

	public void setNumberOfReducePhases(int numberOfReducePhases) {
		this.numberOfReducePhases = numberOfReducePhases;
	}

	public double getTimeForAVG() {
		return (this.afterExecution-this.beforeExecution);
	}
}
