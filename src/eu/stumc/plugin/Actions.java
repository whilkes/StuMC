package eu.stumc.plugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import eu.stumc.plugin.data.PlayerData;
import eu.stumc.plugin.data.PunishmentData;
import eu.stumc.plugin.data.ReportData;

public class Actions {
	
	public static void lookup(CommandSender sender, OfflinePlayer target) throws SQLException {
		List<PunishmentData> punishments = DatabaseOperations.getPunishmentsByUuid(target.getUniqueId());
		
		String playerDisplay = "";
		if (target.isOnline())
			playerDisplay = target.getPlayer().getDisplayName();
		else
			playerDisplay = ChatColor.DARK_AQUA + DatabaseOperations.getNameFromUuid(target.getUniqueId());
		
		if (punishments.isEmpty())
			sender.sendMessage(Strings.NO_PUNISHMENTS_HEADER.replace("$1", playerDisplay));
		else
			sender.sendMessage(Strings.PUNISHMENTS_HEADER.replace("$1", playerDisplay));
		
		for (PunishmentData punishmentData: punishments) {
			String punisher = "";
			UUID punisherUuid = punishmentData.getPunisher();
			if (punisherUuid.toString().equals("00000000-0000-0000-0000-000000000000"))
				punisher = ChatColor.GOLD + "(console)";
			else if (Bukkit.getPlayer(punisherUuid) == null)
				punisher = DatabaseOperations.getNameFromUuid(punisherUuid);
			else
				punisher = Bukkit.getPlayer(punisherUuid).getDisplayName();
			String type = punishmentData.getType();
			String reason = punishmentData.getReason();
			sender.sendMessage(Strings.LIST_PUNISHMENT
					.replace("$1", punisher)
					.replace("$2", type)
					.replace("$3", reason));
			sender.sendMessage(ChatColor.AQUA +
					"Issued " + new Date(punishmentData.getTimestamp() * 1000).toString());
		}
	}
	
	public static void report(Player sender, Player target, String reason) throws SQLException {
		String reporterDisplay = sender.getDisplayName();
		String reportedDisplay = target.getDisplayName();
		
		DatabaseOperations.insertReport(sender.getUniqueId(), target.getUniqueId(),
				reason, System.currentTimeMillis() / 1000);
		
		sendStaffMessage(Strings.REPORT_BROADCAST
				.replace("$1", reporterDisplay)
				.replace("$2", reportedDisplay)
				.replace("$3", reason));
		
		sender.sendMessage(Strings.REPORT_SUBMITTED);
	}
	
	public static void broadcastReportFromOtherServer(UUID reporter, UUID reported, String reason, String server) throws SQLException {
		String reporterDisplay = ChatColor.DARK_AQUA +
				DatabaseOperations.getNameFromUuid(reporter);
		String reportedDisplay = ChatColor.DARK_AQUA +
				DatabaseOperations.getNameFromUuid(reported);
		
		sendStaffMessage(Strings.REPORT_BROADCAST_OTHER_SERVER
				.replace("$1", reporterDisplay)
				.replace("$2", reportedDisplay)
				.replace("$3", reason)
				.replace("$4", server));
	}
	
	public static void getReports(CommandSender sender, int page) throws SQLException {
		List<ReportData> reports = DatabaseOperations.getReports(page);
		sender.sendMessage(Strings.REPORTS_HEADER);
		int i = 1;
		for (ReportData reportData: reports) {
			String server = reportData.getServer();
			long timestamp = reportData.getTimestamp();
			UUID reporter = reportData.getReporter();
			UUID reported = reportData.getReported();
			String reason = reportData.getReason();
			
			String reporterDisplay = "";
			String reportedDisplay = "";
			if (Bukkit.getPlayer(reporter) == null)
				reporterDisplay = DatabaseOperations.getNameFromUuid(reporter);
			else
				reporterDisplay = Bukkit.getPlayer(reporter).getDisplayName();
			
			if (Bukkit.getPlayer(reported) == null)
				reportedDisplay = DatabaseOperations.getNameFromUuid(reported);
			else
				reportedDisplay = Bukkit.getPlayer(reported).getDisplayName();
			
			sender.sendMessage(Strings.LIST_REPORT
					.replace("$1", Integer.toString(i))
					.replace("$2", server)
					.replace("$3", reporterDisplay)
					.replace("$4", reportedDisplay)
					.replace("$5", reason));
			sender.sendMessage(ChatColor.AQUA +
					"Report submitted " +
					new Date(timestamp * 1000).toString());
			i++;
		}
	}
	
	public static void sendStaffMessage(CommandSender sender, String input) {
		String message = "";
		
		if (sender instanceof ConsoleCommandSender)
			message = Strings.STAFF_CHAT
			.replace("$1", ChatColor.translateAlternateColorCodes('&',
					"&6(console)"))
			.replace("$2", input);
		else
			message = Strings.STAFF_CHAT
			.replace("$1", Bukkit.getPlayer(sender.getName()).getDisplayName())
			.replace("$2", input);
		
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("stumc.staff"))
				player.sendMessage(message);
		}
		Bukkit.getLogger().info(message);
	}
	
	public static void sendStaffMessage(String message) {
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("stumc.staff"))
				player.sendMessage(message);
		}
		Bukkit.getLogger().info(message);
	}
	
	public static void sendGlobalMessage(String message) {
		for (Player player: Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
		Bukkit.getLogger().info(message);
	}
	
	public static void getOnlineStaff(CommandSender sender) {
		StringBuilder staff = new StringBuilder();
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("stumc.staff")) {
					staff.append(player.getDisplayName());
					staff.append(", ");
			}
		}
		String staffList = staff.length() > 0 ? staff.substring(0, staff.length() - 2): "";
		if (staff.length() == 0)
			sender.sendMessage(ChatColor.GOLD + "No staff online.");
		else
			sender.sendMessage(ChatColor.GOLD + "Online staff: " +
					staffList);
	}
	
	public static void findPlayer(CommandSender sender, OfflinePlayer target, boolean online) throws SQLException {
		if (online) {
			Player player = target.getPlayer();
			sender.sendMessage(Strings.SEEN_COMMAND_CURRENT
					.replace("$1", player.getDisplayName()));
			
			if (sender instanceof ConsoleCommandSender
					|| Bukkit.getPlayer(sender.getName()).hasPermission("stumc.mod")) {
				Location location = player.getLocation();
				sender.sendMessage(Strings.SEEN_COMMAND_POSITION
						.replace("$1", player.getWorld().getName())
						.replace("$2", Integer.toString(location.getBlockX()))
						.replace("$3", Integer.toString(location.getBlockY()))
						.replace("$4", Integer.toString(location.getBlockZ())));
			}
			
			if (sender instanceof ConsoleCommandSender
					|| Bukkit.getPlayer(sender.getName()).hasPermission("stumc.admin")) {
				sender.sendMessage(Strings.SEEN_COMMAND_IP
						.replace("$1", player.getAddress().getHostString()));
			}
		} else {
			PlayerData playerData = DatabaseOperations.getPlayerDataByUuid(target.getUniqueId());
			if (playerData.isOnline()) {
				sender.sendMessage(Strings.SEEN_COMMAND_OTHER
						.replace("$1", playerData.getName())
						.replace("$2", playerData.getServer()));
			} else {
				sender.sendMessage(Strings.SEEN_COMMAND_OFFLINE
						.replace("$1", playerData.getName())
						.replace("$2", playerData.getServer())
						.replace("$3", new Date(playerData.getTimestamp() * 1000).toString()));
			}
			
			if ((sender instanceof ConsoleCommandSender
					|| Bukkit.getPlayer(sender.getName()).hasPermission("stumc.mod"))
					&& !playerData.isOnline()) {
				sender.sendMessage(Strings.SEEN_COMMAND_POSITION
						.replace("$1", playerData.getWorld())
						.replace("$2", Integer.toString(playerData.lastX()))
						.replace("$3", Integer.toString(playerData.lastY()))
						.replace("$4", Integer.toString(playerData.lastZ())));
			}
			
			if (sender instanceof ConsoleCommandSender
					|| Bukkit.getPlayer(sender.getName()).hasPermission("stumc.admin")) {
				sender.sendMessage(Strings.SEEN_COMMAND_IP
						.replace("$1", playerData.getIpAddress()));
			}
		}
	}
	
}
