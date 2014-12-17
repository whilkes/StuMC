package eu.stumc.plugin.common;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.stumc.plugin.PlayerData;

public class PlayerCommands{
	
	public static void findPlayer(String user, CommandSender sender) throws SQLException{
		Player player = Bukkit.getPlayer(user);
		if (player == null){
			PlayerData data = Players.getPlayerDataFromDatabase(Bukkit.getOfflinePlayer(user).getUniqueId());
			if (data == null){
				sender.sendMessage(ChatColor.RED+"Could not find the user "+user);
				return;
			}
			String IP = data.getIP();
			String server = data.getServer();
			boolean online = data.isOnline();
			java.util.Date time = new java.util.Date(data.getTime() * 1000);
			
			if (online){
				sender.sendMessage(ChatColor.DARK_AQUA+user+ChatColor.GOLD+" is online on "+ChatColor.DARK_AQUA+server+" .");
			}else{
				sender.sendMessage(ChatColor.DARK_AQUA+user+ChatColor.GOLD+" is offline.");
				sender.sendMessage(ChatColor.GOLD+"Last seen on "+ChatColor.DARK_AQUA+server+ChatColor.GOLD+" on "+ChatColor.DARK_AQUA+time);
				if (sender.hasPermission("stumc.staff"))
					sender.sendMessage(ChatColor.GOLD+"Last IP: "+IP);
			}		
		}else{
			sender.sendMessage(player.getDisplayName()+ChatColor.GOLD+" is online.");
			if (sender.hasPermission("stumc.staff"))
				sender.sendMessage(ChatColor.GOLD+"IP: "+player.getAddress());
		}
	}

}
