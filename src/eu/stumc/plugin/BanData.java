package eu.stumc.plugin;

import java.util.UUID;

public class BanData{
	
	private UUID banned;
	private String reason;
	private long time;
	private long expires;
	
	public BanData(UUID banned, String reason, long time, long expires){
		this.banned = banned;
		this.expires = expires;
		this.reason = reason;
		this.time = time;
	}
	
	public UUID getBanned(){
		return banned;
	}
	
	public String getReason(){
		return reason;
	}
	
	public long getTime(){
		return time;
	}
	
	public long getExpiry(){
		return expires;
	}

}
