package eu.stumc.plugin.events;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import eu.stumc.plugin.DatabaseOperations;
import eu.stumc.plugin.threads.IssueUnservedPunishment;

public class JoinEvent implements Listener {
	
	private eu.stumc.plugin.StuMC plugin;
	private UUID uuid;
	private String address;
	
	public JoinEvent(eu.stumc.plugin.StuMC plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();
		uuid = player.getUniqueId();
		address = player.getAddress().getHostString();
		BukkitTask task = new IssueUnservedPunishment(player).runTaskLater(plugin, 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					DatabaseOperations.setPlayerOnlineInDatabase(uuid, address);
				} catch (SQLException e) {
					Bukkit.getLogger().severe("Failed to query database: " + e);
					e.printStackTrace();
				}
			}
		}.runTaskLater(plugin, 20L);
	}
}
