package eu.stumc.plugin.listeners;

import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.stumc.plugin.common.Players;

public class JoinEventListener implements Listener{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) throws SQLException{
		if (Players.isUserInDatabase(event.getPlayer().getUniqueId()))
			Players.updatePlayerInDatabase(event.getPlayer(), 1);
		else
			Players.addPlayerToDatabase(event.getPlayer());
	}

}
