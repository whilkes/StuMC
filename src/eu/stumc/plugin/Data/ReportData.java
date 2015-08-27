package eu.stumc.plugin.Data;

import java.util.UUID;

public class ReportData {
	
	private UUID reporter;
	private UUID reported;
	private String reason;
	private long timestamp;
	private String server;
	
	public ReportData(UUID reporter, UUID reported, String reason,
			long timestamp, String server) {
		this.reporter = reporter;
		this.reported = reported;
		this.reason = reason;
		this.timestamp = timestamp;
		this.server = server;
	}

	public UUID getReporter() {
		return reporter;
	}

	public UUID getReported() {
		return reported;
	}

	public String getReason() {
		return reason;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getServer() {
		return server;
	}
	
}
