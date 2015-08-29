package eu.stumc.plugin.data;

import java.util.UUID;

public class PlayerData {
	
	private UUID uuid;
	private String name;
	private String ipAddress;
	private String server;
	private boolean isOnline;
	private long timestamp;
	private String world;
	private int lastX;
	private int lastY;
	private int lastZ;
	
	public PlayerData(UUID uuid, String name, String ipAddress,
			String server, boolean isOnline, long timestamp,
			String world, int lastX, int lastY, int lastZ) {
		this.uuid = uuid;
		this.name = name;
		this.ipAddress = ipAddress;
		this.server = server;
		this.isOnline = isOnline;
		this.timestamp = timestamp;
		this.world = world;
		this.lastX = lastX;
		this.lastY = lastY;
		this.lastZ = lastZ;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public String getServer() {
		return server;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getWorld() {
		return world;
	}
	
	public int lastX() {
		return lastX;
	}
	
	public int lastY() {
		return lastY;
	}
	
	public int lastZ() {
		return lastZ;
	}
	
}
