package eu.stumc.plugin.events;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.stumc.plugin.DatabaseOperations;
import eu.stumc.plugin.Strings;

public class JoinEvent implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		try {
			DatabaseOperations.setPlayerOnlineInDatabase(player.getUniqueId(),
					player.getAddress().getHostString());
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Failed to query database: "+e);
			e.printStackTrace();
			player.kickPlayer(Strings.CONNECT_FAIL_DATABASE_ERROR);
		}
	}

}
