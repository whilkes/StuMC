package eu.stumc.plugin.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reports{
	
	private static eu.stumc.plugin.StuMC plugin;
	
	public Reports(eu.stumc.plugin.StuMC plugin){
		Reports.plugin = plugin;
	}
	
	public static void submitReport(Player player, String reason, CommandSender sender){
		String serverName = plugin.getServerName();
		Player reporter = Bukkit.getPlayer(sender.getName());
		plugin.insertQuery("INSERT INTO reports (reporter_UUID, reported_UUID, reason, time, server) VALUES ("
				+ "`"+player.getUniqueId()+"`,"
				+ "`"+reporter.getUniqueId()+"`,"
				+ "`"+reason+"`,"
				+ "`"+System.currentTimeMillis() / 1000L+"`,"
				+ "`"+serverName+"`);");
		sender.sendMessage(ChatColor.AQUA+"Thank you, your report has been submitted.");
		Players.broadcastReport(player, reporter, reason);
	}
	
	public static void getReports(CommandSender sender) throws SQLException{
		getReports(sender, 1);
	}
	
	public static void getReports(CommandSender sender, int page) throws SQLException{
		int start = page * 15;
		sender.sendMessage(ChatColor.AQUA+"- - - -"+ChatColor.GOLD+" Reports "+ChatColor.AQUA+"- - - -");
		ResultSet result = plugin.selectQuery("SELECT * FROM reports ORDER BY time DESC LIMIT "+start+", 15;");
		while (result.next()){
			Players.listReport(UUID.fromString(result.getString("reporter_UUID")),
					UUID.fromString(result.getString("reported_UUID")),
					result.getString("reason"), result.getString("server"), result.getLong("time"));
		}
	}

}
