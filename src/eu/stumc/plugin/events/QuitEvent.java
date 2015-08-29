package eu.stumc.plugin.events;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.stumc.plugin.DatabaseOperations;

public class QuitEvent implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Location location = event.getPlayer().getLocation();
		try {
			DatabaseOperations.setPlayerOfflineInDatabase(player.getUniqueId(),
					location.getBlockX(), location.getBlockY(), location.getBlockZ(),
					player.getWorld().getName());
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Failed to query database: "+e);
		}
	}

}
