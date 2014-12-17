package eu.stumc.plugin;

import java.util.UUID;

public class PlayerData{
	
	private UUID uuid;
	private String username;
	private String IP;
	private int x;
	private int y;
	private int z;
	private long time;
	private String server;
	private boolean online;
	
	public PlayerData(UUID uuid, String username, String IP, int x, int y, int z, long time, String server, boolean online){
		this.uuid = uuid;
		this.username = username;
		this.IP = IP;
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
		this.server = server;
		this.online = online;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	
}
