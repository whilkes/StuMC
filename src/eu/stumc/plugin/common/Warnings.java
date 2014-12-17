package eu.stumc.plugin.common;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Warnings{
	
	private static eu.stumc.plugin.StuMC plugin;
	
	public Warnings(eu.stumc.plugin.StuMC plugin){
		Warnings.plugin = plugin;
	}
	
	public static void issueWarning(String user, String reason, CommandSender sender) throws SQLException{
		Player player = Bukkit.getPlayer(user);
		if (player == null){
			if (!Players.isUserInDatabase(Bukkit.getOfflinePlayer(user).getUniqueId())){
				sender.sendMessage(ChatColor.RED+"That player does not exist.");
				return;
			}
			plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, server) "
					+ "VALUES (`"+Bukkit.getOfflinePlayer(user).getUniqueId()+"`, "
					+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
					+ "`"+reason+"`, "
					+ "`warn`, "
					+ "`"+System.currentTimeMillis() / 1000L+"`, "
					+ "`"+plugin.getServerName()+"`);");
		}else{
			plugin.insertQuery("INSERT INTO punishments (punished_UUID, punisher_UUID, reason, type, time, server) "
					+ "VALUES (`"+player.getUniqueId()+"`, "
					+ "`"+Bukkit.getPlayer(sender.getName()).getUniqueId()+"`, "
					+ "`"+reason+"`, "
					+ "`warn`, "
					+ "`"+System.currentTimeMillis() / 1000L+"`, "
					+ "`"+plugin.getServerName()+"`);");
		}
		String punisherDisplay = Players.getDisplayName(sender);
		String punishedDisplay = Players.getDisplayName(user);
		
		Players.broadcastPunishment(punisherDisplay, punishedDisplay, "warn", reason);
		if (player != null)
			Players.warnPlayer(player, reason);
	}

}
