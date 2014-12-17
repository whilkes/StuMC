package eu.stumc.plugin.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Punishments{
	
	private static eu.stumc.plugin.StuMC plugin;
	
	public Punishments(eu.stumc.plugin.StuMC plugin){
		Punishments.plugin = plugin;
	}
	
	public static void issuePunishment(String user, String reason, CommandSender sender) throws SQLException{
		Player player = Bukkit.getPlayer(user);
		ResultSet result;
		boolean kicked = false;
		boolean banned = false;
		if (player == null){
			if (!Players.isUserInDatabase(Bukkit.getOfflinePlayer(user).getUniqueId())){
				sender.sendMessage(ChatColor.RED+"That player does not exist.");
				return;
			}
			result = plugin.selectQuery("SELECT * FROM punishments WHERE punished_UUID = `"+Bukkit.getOfflinePlayer(user).getUniqueId()+"`;");
		}else
			result = plugin.selectQuery("SELECT * FROM punishments WHERE punished_UUID = `"+player.getUniqueId()+"`;");
		
		while (result.next()){
			if (result.getString("type").equals("kick"))
				kicked = true;
			if (result.getString("type").equals("ban") || result.getString("type").equals("tempban"))
				banned = true;
		}
		
		String punisherDisplay = Players.getDisplayName(sender);
		String punishedDisplay = Players.getDisplayName(user);
		
		if (banned){
			if (player == null)
				plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, expiry, server) "
						+ "VALUES (`"+Bukkit.getOfflinePlayer(user).getUniqueId()+"`, "
						+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
						+ "`"+reason+"`, "
						+ "`ban`, "
						+ "`"+System.currentTimeMillis() / 1000L+"`, "
						+ "`0`, "
						+ "`"+plugin.getServerName()+"`);");
			else
				plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, expiry, server) "
						+ "VALUES (`"+player.getUniqueId()+"`, "
						+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
						+ "`"+reason+"`, "
						+ "`ban`, "
						+ "`"+System.currentTimeMillis() / 1000L+"`, "
						+ "`0`, "
						+ "`"+plugin.getServerName()+"`);");
			Players.broadcastPunishment(punisherDisplay, punishedDisplay, "ban", reason);
		}else if (kicked){
			if (player == null)
				plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, expiry, server) "
						+ "VALUES (`"+Bukkit.getOfflinePlayer(user).getUniqueId()+"`, "
						+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
						+ "`"+reason+"`, "
						+ "`tempban`, "
						+ "`"+System.currentTimeMillis() / 1000L+"`, "
						+ "`"+((System.currentTimeMillis() / 1000L) + (86400 * 7))+"`, "
						+ "`"+plugin.getServerName()+"`);");
			else
				plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, expiry, server) "
						+ "VALUES (`"+player.getUniqueId()+"`, "
						+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
						+ "`"+reason+"`, "
						+ "`tempban`, "
						+ "`"+System.currentTimeMillis() / 1000L+"`, "
						+ "`"+((System.currentTimeMillis() / 1000L) + (86400 * 7))+"`, "
						+ "`"+plugin.getServerName()+"`);");
			Players.broadcastPunishment(punisherDisplay, punishedDisplay, "tempban", reason);
		}else{
			if (player == null)
				plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, server) "
						+ "VALUES (`"+Bukkit.getOfflinePlayer(user).getUniqueId()+"`, "
						+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
						+ "`"+reason+"`, "
						+ "`kick`, "
						+ "`"+System.currentTimeMillis() / 1000L+"`, "
						+ "`"+plugin.getServerName()+"`);");
			else
				plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, server) "
						+ "VALUES (`"+player.getUniqueId()+"`, "
						+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
						+ "`"+reason+"`, "
						+ "`kick`, "
						+ "`"+System.currentTimeMillis() / 1000L+"`, "
						+ "`"+plugin.getServerName()+"`);");
			Players.broadcastPunishment(punisherDisplay, punishedDisplay, "kick", reason);
		}
		
		/*if (player != null)
			Players.warnPlayer(player, reason);*/
	}
	
	
	public static void issueBan(String user, String reason, CommandSender sender) throws SQLException{
		Player player = Bukkit.getPlayer(user);
		if (player == null){
			if (!Players.isUserInDatabase(Bukkit.getOfflinePlayer(user).getUniqueId())){
				sender.sendMessage(ChatColor.RED+"That player does not exist.");
				return;
			}
		}
		
		String punisherDisplay = Players.getDisplayName(sender);
		String punishedDisplay = Players.getDisplayName(user);
		
		if (player == null)
			plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, expiry, server) "
					+ "VALUES (`"+Bukkit.getOfflinePlayer(user).getUniqueId()+"`, "
					+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
					+ "`"+reason+"`, "
					+ "`ban`, "
					+ "`"+System.currentTimeMillis() / 1000L+"`, "
					+ "`0`, "
					+ "`"+plugin.getServerName()+"`);");
		else
			plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, expiry, server) "
					+ "VALUES (`"+player.getUniqueId()+"`, "
					+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
					+ "`"+reason+"`, "
					+ "`ban`, "
					+ "`"+System.currentTimeMillis() / 1000L+"`, "
					+ "`0`, "
					+ "`"+plugin.getServerName()+"`);");
		Players.broadcastPunishment(punisherDisplay, punishedDisplay, "ban", reason);
	}
	

}
