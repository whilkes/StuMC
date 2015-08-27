package eu.stumc.plugin.Data;

import java.util.UUID;

public class PunishmentData {
	
	private int id;
	private String type;
	private String reason;
	private UUID punisher;
	private long timestamp;
	private long expiry;
	private String server;
	
	public PunishmentData(int id, String type, String reason, UUID punisher,
			long timestamp, long expiry, String server) {
		this.id = id;
		this.type = type;
		this.reason = reason;
		this.punisher = punisher;
		this.timestamp = timestamp;
		this.expiry = expiry;
		this.server = server;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}
	
	public UUID getPunisher() {
		return punisher;
	}

	public String getReason() {
		return reason;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getExpiry() {
		return expiry;
	}

	public String getServer() {
		return server;
	}
	
}
