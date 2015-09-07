package eu.stumc.plugin.threads;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import eu.stumc.plugin.Actions;
import eu.stumc.plugin.DatabaseOperations;
import eu.stumc.plugin.Strings;
import eu.stumc.plugin.data.PunishmentData;

public class IssueUnservedPunishment extends BukkitRunnable {

	Player player;

	public IssueUnservedPunishment(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			PunishmentData unservedPunishment = DatabaseOperations
					.getFirstUnservedPunishment(player.getUniqueId());
			if (unservedPunishment != null
					&& unservedPunishment.getType().equals("kick")) {
				player.kickPlayer(Strings.KICKED.replace("$1",
						unservedPunishment.getReason()));
				DatabaseOperations.setPunishmentServed(unservedPunishment
						.getId());
				return;
			} else if (unservedPunishment != null
					&& unservedPunishment.getType().equals("warn")) {
				player.sendMessage(Strings.WARNED_TITLE);
				player.sendMessage(Strings.WARNED_1.replace("$1",
						unservedPunishment.getReason()));
				player.sendMessage(Strings.WARNED_2.replace("$1",
						unservedPunishment.getReason()));
				player.sendMessage(Strings.WARNED_3.replace("$1",
						unservedPunishment.getReason()));
				player.sendMessage(Strings.WARNED_TITLE);
				player.playSound(player.getLocation(), Sound.WITHER_DEATH, 10,
						1);
				DatabaseOperations.setPunishmentServed(unservedPunishment
						.getId());
			}
		} catch (SQLException e) {
				Actions.sendStaffMessage(ChatColor.RED +
						"Failed to process unserved punishment. See console.");
				Bukkit.getLogger().severe("Failed to process unserved punishments: "+e);
				e.printStackTrace();
		}
	}

}
