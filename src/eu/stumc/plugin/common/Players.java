package eu.stumc.plugin.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import eu.stumc.plugin.BanData;
import eu.stumc.plugin.PlayerData;

public class Players{
	
	private static eu.stumc.plugin.StuMC plugin;
	
	public Players(eu.stumc.plugin.StuMC plugin){
		Players.plugin = plugin;
	}
	
	public static void addPlayerToDatabase(Player player){
		plugin.insertQuery("INSERT INTO users (UUID, last_username, last_ip, last_timestamp, server, online) "
				+ "VALUES (`"+player.getUniqueId() + "`,"
						+ "`"+player.getName() + "`,"
						+ "`"+player.getAddress().toString()+ "`,"
						+ System.currentTimeMillis() / 1000L + ","
						+ "`"+plugin.getServerName() + "`,"
						+ "1);");
	}
	
	public static void updatePlayerInDatabase(Player player, int online){
		plugin.insertQuery("UPDATE users SET last_username = `"+player.getName()+"`, "
				+ "last_ip = `" + player.getAddress().toString() + "`,"
				+ "last_timestamp = " + System.currentTimeMillis() / 1000L +", "
				+ "server = `" + plugin.getServerName() + "`, "
				+ "online = " + online + " "
				+ "WHERE UUID = `"+ player.getUniqueId() + "`;");
	}
	
	public static boolean isUserInDatabase(UUID uuid) throws SQLException{
		ResultSet result = plugin.selectQuery("SELECT UUID FROM users WHERE UUID = `"+uuid+"`;");
		if (result.next())
			return true;
		return false;
	}
	
	public static PlayerData getPlayerDataFromDatabase(UUID uuid) throws SQLException{
		ResultSet result = plugin.selectQuery("SELECT * FROM users WHERE UUID = `"+uuid+"`;");
		while (result.next()){
			int online = result.getInt("online");
			boolean isOnline;
			if (online == 1)
				isOnline = true;
			else
				isOnline = false;
			return new PlayerData(uuid, result.getString("last_username"), result.getString("last_IP"), result.getInt("last_x"), result.getInt("last_y"), result.getInt("last_z"), result.getInt("last_timestamp"), result.getString("server"), isOnline);
		}
		return null;
	}
	
	/*public static PlayerData getOnlinePlayerData(Player player){
		
		return new PlayerData(null, null, null, 0, 0, 0, 0, null, false);
	}*/
	
	public static BanData getPlayerBanData(UUID uuid) throws SQLException{
		BanData data = null;
		ResultSet result = plugin.selectQuery("SELECT * FROM punishments WHERE punished_UUID = `"+uuid+"` AND (type = `ban` OR type = `tempban`);");
		while (result.next()){
			data = new BanData(uuid, result.getString("reason"), result.getLong("time"), result.getLong("expiry") * 1000L);
		}
		return data;
	}
	
	public static String getDisplayName(String user){
		Player player = Bukkit.getPlayer(user);
		if (player == null)
			return ChatColor.DARK_AQUA+user;
		else
			return player.getDisplayName();
	}
	
	public static String getDisplayName(CommandSender sender){
		if (sender instanceof ConsoleCommandSender)
			return ChatColor.GOLD+"(console)";
		else{
			Player player = Bukkit.getPlayer(sender.getName());
			if (player == null)
				return ChatColor.DARK_AQUA+sender.getName();
			else
				return player.getDisplayName();
		}
	}
	
	public static void broadcastPunishment(String punisherDisplay, String punishedDisplay, String type, String reason){
		switch (type){
		case "warn":
			for (Player p: Bukkit.getOnlinePlayers())
				if (p.hasPermission("stumc.staff"))
					p.sendMessage(ChatColor.DARK_AQUA+"["+ChatColor.GOLD+"S"+ChatColor.DARK_AQUA+"] "+punisherDisplay+ChatColor.GOLD+" warned "+ChatColor.DARK_AQUA+punishedDisplay+ChatColor.RED+": "+reason);
			break;
		case "kick":
			for (Player plr: Bukkit.getOnlinePlayers())
				plr.sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"*"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA+punisherDisplay+ChatColor.YELLOW+" -> "+ChatColor.RED+"Banned for 7 days"+ChatColor.YELLOW+" -> "+ChatColor.DARK_AQUA+punishedDisplay+ChatColor.YELLOW+" -> "+ChatColor.AQUA+reason);
			break;
		case "tempban":
			for (Player plr: Bukkit.getOnlinePlayers())
				plr.sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"*"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA+punisherDisplay+ChatColor.YELLOW+" -> "+ChatColor.RED+"Banned for 7 days"+ChatColor.YELLOW+" -> "+ChatColor.DARK_AQUA+punishedDisplay+ChatColor.YELLOW+" -> "+ChatColor.AQUA+reason);
			break;
		case "ban":
			for (Player plr: Bukkit.getOnlinePlayers())
				plr.sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"*"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA+punisherDisplay+ChatColor.YELLOW+" -> "+ChatColor.RED+"Permanent ban"+ChatColor.YELLOW+" -> "+ChatColor.DARK_AQUA+punishedDisplay+ChatColor.YELLOW+" -> "+ChatColor.AQUA+reason);
			break;
		default:
			try{
				throw new RuntimeException("Unknown punishment type: "+type);
			}catch (RuntimeException e){
				plugin.getLogger().severe("Exception occurred: "+e);
				e.printStackTrace();
			}
		}
	}
	
	public static void warnPlayer(Player player, String reason){
		player.playSound(player.getLocation(), Sound.WITHER_DEATH, 10, 1);
		player.sendMessage(ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.AQUA+"Warning "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] ");
		player.sendMessage(ChatColor.GOLD+"~ ~ ~ "+reason+"~ ~ ~");
		player.sendMessage(ChatColor.RED+"~ ~ ~ "+reason+"~ ~ ~");
		player.sendMessage(ChatColor.GREEN+"~ ~ ~ "+reason+"~ ~ ~");
		player.sendMessage(ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.AQUA+"Warning "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] "+ChatColor.GOLD+""+ChatColor.MAGIC+"["+ChatColor.RED+""+ChatColor.MAGIC+"*"+ChatColor.GOLD+""+ChatColor.MAGIC+"] ");
	}
	
	public static void punishPlayer(Player player, String reason, String type){
		switch (type){
		case "ban":
			player.kickPlayer(ChatColor.RED+"Permanently banned"+ChatColor.YELLOW+" -> "+ChatColor.AQUA+reason+"\n"+ChatColor.GOLD+"Appeal at "+ChatColor.RED+"http://www.stumc.eu/support/");
			break;
		case "tempban":
			player.kickPlayer(ChatColor.RED+"Banned for 7 days"+ChatColor.YELLOW+" -> "+ChatColor.AQUA+reason+"\n"+ChatColor.GOLD+"Appeal at "+ChatColor.RED+"http://www.stumc.eu/support/");
			break;
		case "kick":
			player.kickPlayer(ChatColor.GOLD+"Kicked"+ChatColor.YELLOW+" -> "+ChatColor.AQUA+reason+"\n"+ChatColor.GOLD+"Appeal at "+ChatColor.RED+"http://www.stumc.eu/support/");
			break;
		default:
			try{
				throw new RuntimeException("Unknown punishment type: "+type);
			}catch (RuntimeException e){
				plugin.getLogger().severe("Exception occurred: "+e);
				e.printStackTrace();
			}
		}
	}
	
	public static void broadcastReport(Player reported, Player reporter, String reason){
		for (Player p: Bukkit.getOnlinePlayers())
			if (p.hasPermission("stumc.staff"))
				p.sendMessage(ChatColor.DARK_AQUA+"["+ChatColor.GOLD+"R"+ChatColor.DARK_AQUA+"] "+reporter.getDisplayName()+ChatColor.GOLD+" reports "+ChatColor.DARK_AQUA+reported.getDisplayName()+ChatColor.RED+": "+reason);
		Bukkit.getLogger().info(reporter+" reported "+reporter+": "+reason);
	}
	
	public static void broadcastReport(UUID reporter, UUID reported, String reason, String server){
		
	}
	
	public static void listReport(UUID reporter, UUID reported, String reason, String server, long time){
		
	}

}
