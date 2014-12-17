package eu.stumc.plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.stumc.plugin.common.Players;

public class QuitEventListener implements Listener{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event){
		Players.updatePlayerInDatabase(event.getPlayer(), 0);
	}

}
