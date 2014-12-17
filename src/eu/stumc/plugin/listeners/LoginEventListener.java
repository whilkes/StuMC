package eu.stumc.plugin.listeners;

import java.sql.SQLException;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import eu.stumc.plugin.BanData;
import eu.stumc.plugin.common.Players;

public class LoginEventListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onLoginHigh(AsyncPlayerPreLoginEvent event) throws SQLException{
		BanData data = Players.getPlayerBanData(event.getUniqueId());
		if (data == null){
			event.allow();
			return;
		}else{
			String reason = "";
			long expiry = data.getExpiry();
			if (expiry == 0){
				reason = ChatColor.RED+"Permanently banned"+ChatColor.YELLOW+" -> "+ChatColor.AQUA+data.getReason()+"\n"+ChatColor.GOLD+"Appeal at "+ChatColor.RED+"http://www.stumc.eu/support/";
				event.disallow(Result.KICK_BANNED, reason);
			}else if (System.currentTimeMillis() >= expiry)
				event.allow();
			else{
				reason = ChatColor.RED+"Banned for 7 days"+ChatColor.YELLOW+" -> "+ChatColor.AQUA+data.getReason()+"\n"+ChatColor.GOLD+"Expires at "+ChatColor.RED+(new Date(expiry))+"\n"+ChatColor.GOLD+"Appeal at "+ChatColor.RED+"http://www.stumc.eu/support/";
				event.disallow(Result.KICK_BANNED, reason);
			}
		}
	}

}
