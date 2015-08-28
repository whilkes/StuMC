package eu.stumc.plugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import eu.stumc.plugin.data.PunishmentData;

public class Punishments {

	public static void warn(CommandSender sender, OfflinePlayer target,
			String reason, boolean online) throws SQLException {
		String punisherDisplay = "";
		if (sender instanceof ConsoleCommandSender) {
			DatabaseOperations.insertPunishment(
					UUID.fromString("00000000-0000-0000-0000-000000000000"),
					target.getUniqueId(), "warn", reason,
					(System.currentTimeMillis() / 1000), 0);
			punisherDisplay = ChatColor.GOLD + "(console)";
		} else {
			DatabaseOperations.insertPunishment(
					Bukkit.getPlayer(sender.getName()).getUniqueId(),
					target.getUniqueId(), "warn", reason,
					(System.currentTimeMillis() / 1000), 0);
			punisherDisplay = Bukkit.getPlayer(sender.getName())
					.getDisplayName();
		}

		String punishedDisplay = "";
		if (online) {
			Player player = target.getPlayer();
			punishedDisplay = player.getDisplayName();
			player.sendMessage(Strings.WARNED_TITLE);
			player.sendMessage(Strings.WARNED_1.replace("$1", reason));
			player.sendMessage(Strings.WARNED_2.replace("$1", reason));
			player.sendMessage(Strings.WARNED_3.replace("$1", reason));
			player.sendMessage(Strings.WARNED_TITLE);
			player.playSound(player.getLocation(), Sound.WITHER_DEATH, 10, 1);
		} else
			punishedDisplay = ChatColor.DARK_AQUA + target.getName();

		broadcastPunishment(punisherDisplay, punishedDisplay, "warn", reason);
	}

	/*
	 * int type 0 = auto-determine 1 = kick 2 = 7-day 3 = perma
	 */
	public static void punish(CommandSender sender, OfflinePlayer target,
			String reason, boolean online, int type) throws SQLException {
		long expiry = 0;
		if (type == 0) {
			List<PunishmentData> punishments = DatabaseOperations
					.getPunishmentsByUuid(target.getUniqueId());
			for (PunishmentData punishmentData : punishments) {
				if ((punishmentData.getType().equals("tempban") || punishmentData
						.getType().equals("ban")))
					type = 3;
				else if (punishmentData.getType().equals("kick") && type < 2)
					type = 2;
				else if (type == 0)
					type = 1;
			}
			if (type == 0)
				type = 1;
		}

		String punisherDisplay = "";
		UUID uuid = null;
		if (sender instanceof ConsoleCommandSender) {
			punisherDisplay = ChatColor.GOLD + "(console)";
			uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
		} else {
			punisherDisplay = Bukkit.getPlayer(sender.getName())
					.getDisplayName();
			uuid = Bukkit.getPlayer(sender.getName()).getUniqueId();
		}

		String punishedDisplay = "";
		if (online)
			punishedDisplay = target.getPlayer().getDisplayName();
		else
			punishedDisplay = ChatColor.DARK_AQUA + target.getName();

		String message = "";
		String typeStr = "";
		switch (type) {
		case 1:
			message = Strings.KICKED.replace("$1", reason);
			typeStr = "kick";
			break;
		case 2:
			expiry = System.currentTimeMillis() / 1000 + 604800;
			message = Strings.TEMPBANNED.replace("$1", reason).replace("$2",
					new Date(expiry).toString());
			typeStr = "tempban";
			break;
		case 3:
			message = Strings.PERMABANNED.replace("$1", reason);
			typeStr = "ban";
			break;
		default:
			sender.sendMessage(ChatColor.RED
					+ "Error occurred setting punishment type. See console.");
			Bukkit.getLogger().severe(
					"Error occurred setting punishment type: " + "Type: "
							+ type + ", " + typeStr);
			throw new RuntimeException(
					"Error occurred setting punishment type: " + "Type: "
							+ type + ", " + typeStr);
		}

		if (online)
			target.getPlayer().kickPlayer(message);

		DatabaseOperations.insertPunishment(uuid, target.getUniqueId(),
				typeStr, reason, System.currentTimeMillis() / 1000, expiry);
		broadcastPunishment(punisherDisplay, punishedDisplay, typeStr, reason);
	}

	public static void broadcastPunishment(String punisherDisplay,
			String punishedDisplay, String type, String reason) {
		String message = "";
		switch (type) {
		case "warn":
			message = Strings.WARN_BROADCAST.replace("$1", punisherDisplay)
					.replace("$2", punishedDisplay).replace("$3", reason);
			Actions.sendStaffMessage(message);
			break;
		case "kick":
			message = Strings.PUNISHMENT_BROADCAST
					.replace("$1", punisherDisplay).replace("$2", "Kicked")
					.replace("$3", punishedDisplay).replace("$4", reason);
			Actions.sendGlobalMessage(message);
			break;
		case "tempban":
			message = Strings.PUNISHMENT_BROADCAST
					.replace("$1", punisherDisplay).replace("$2", "7 day ban")
					.replace("$3", punishedDisplay).replace("$4", reason);
			Actions.sendGlobalMessage(message);
			break;
		case "ban":
			message = Strings.PUNISHMENT_BROADCAST
					.replace("$1", punisherDisplay)
					.replace("$2", "Permanent ban")
					.replace("$3", punishedDisplay).replace("$4", reason);
			Actions.sendGlobalMessage(message);
			break;
		default:
			Actions.sendStaffMessage(ChatColor.RED
					+ "Error occurred handling punishment broadcast. See console.");
			Bukkit.getLogger().severe(
					"Error occurred handling punishment broadcast: "
							+ "Unrecognised punishment type: " + type);
			throw new RuntimeException(
					"Error occurred handling punishment broadcast: "
							+ "Unrecognised punishment type: " + type);
		}
	}

	public static void punishFromOtherServer(UUID punisher, UUID punished,
			String reason, String server, String type, long expiry) {
		OfflinePlayer punisherPlr = Bukkit.getOfflinePlayer(punisher);
		String punisherDisplay = "";
		if (punisher.toString().equals(
				"00000000-0000-0000-0000-000000000000"))
			punisherDisplay = ChatColor.GOLD + "(console)";
		else if (punisherPlr.getPlayer() == null)
			punisherDisplay = punisherPlr.getName();
		else
			punisherDisplay = punisherPlr.getPlayer().getDisplayName();

		OfflinePlayer punishedPlr = Bukkit.getOfflinePlayer(punished);
		String punishedDisplay = "";
		if (punishedPlr.getPlayer() == null)
			punishedDisplay = punishedPlr.getName();
		else
			punishedDisplay = punishedPlr.getPlayer().getDisplayName();

		if (punishedPlr.getPlayer() != null) {
			String message = "";
			switch (type) {
			case "kick":
				message = Strings.KICKED.replace("$1", reason);
				break;
			case "tempban":
				message = Strings.TEMPBANNED.replace("$1", reason).replace(
						"$2", new Date(expiry).toString());
				break;
			case "ban":
				message = Strings.PERMABANNED.replace("$1", reason);
				break;
			default:
				Actions.sendStaffMessage(ChatColor.RED
						+ "Error occurred handling punishment. See console.");
				Bukkit.getLogger().severe(
						"Error occurred handling punishment: "
								+ "Unrecognised punishment type: " + type);
				throw new RuntimeException(
						"Error occurred handling punishment: "
								+ "Unrecognised punishment type: " + type);
			}
			punishedPlr.getPlayer().kickPlayer(message);
		}
		broadcastPunishmentFromOtherServer(punisherDisplay, punishedDisplay, type, reason, server);
	}
	
	public static void broadcastPunishmentFromOtherServer(String punisherDisplay,
			String punishedDisplay, String type, String reason, String server) {
		String message = "";
		switch (type) {
		case "warn":
			message = Strings.WARN_BROADCAST_OTHER_SERVER.replace("$1", punisherDisplay)
					.replace("$2", punishedDisplay).replace("$3", reason)
					.replace("$4", server);
			Actions.sendStaffMessage(message);
			break;
		case "kick":
			message = Strings.PUNISHMENT_BROADCAST_OTHER_SERVER
					.replace("$1", punisherDisplay).replace("$2", "Kicked")
					.replace("$3", punishedDisplay).replace("$4", reason)
					.replace("$5", server);
			Actions.sendGlobalMessage(message);
			break;
		case "tempban":
			message = Strings.PUNISHMENT_BROADCAST_OTHER_SERVER
					.replace("$1", punisherDisplay).replace("$2", "7 day ban")
					.replace("$3", punishedDisplay).replace("$4", reason)
					.replace("$5", server);
			Actions.sendGlobalMessage(message);
			break;
		case "ban":
			message = Strings.PUNISHMENT_BROADCAST_OTHER_SERVER
					.replace("$1", punisherDisplay)
					.replace("$2", "Permanent ban")
					.replace("$3", punishedDisplay).replace("$4", reason)
					.replace("$5", server);
			Actions.sendGlobalMessage(message);
			break;
		default:
			Actions.sendStaffMessage(ChatColor.RED
					+ "Error occurred handling punishment broadcast. See console.");
			Bukkit.getLogger().severe(
					"Error occurred handling punishment broadcast: "
							+ "Unrecognised punishment type: " + type);
			throw new RuntimeException(
					"Error occurred handling punishment broadcast: "
							+ "Unrecognised punishment type: " + type);
		}
	}

}
