package eu.stumc.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

import eu.stumc.plugin.common.*;

public class Commands{
	
	@Command(aliases = {"warn"}, desc = "Warns a player", usage = "<player> <reason>", min = 2, max = -1)
	public static void warnCommand(final CommandContext args, CommandSender sender) throws Exception{
		String arg0 = args.getString(0);
		String reason = args.getJoinedStrings(1);
		
		Warnings.issueWarning(arg0, reason, sender);
	}
	
	@Command(aliases = {"punish", "p"}, desc = "Punishes a player", usage = "<player> <reason>", min = 2, max = -1)
	public static void punishCommand(final CommandContext args, CommandSender sender) throws Exception{
		String arg0 = args.getString(0);
		String reason = args.getJoinedStrings(1);
		
		Punishments.issuePunishment(arg0, reason, sender);
	}
	
	@Command(aliases = {"pb"}, desc = "Permanently bans a player", usage = "<player> <reason>", min = 2, max = -1)
	public static void permaBanCommand(final CommandContext args, CommandSender sender) throws Exception{
		String arg0 = args.getString(0);
		String reason = args.getJoinedStrings(1);
		
		Punishments.issueBan(arg0, reason, sender);
	}
	
	@Command(aliases = {"lookup","l"}, desc = "Lookup a player's punishment history", usage = "<player>", min = 1, max = 1)
	public static void lookupCommand(final CommandContext args, CommandSender sender) throws Exception{
		String arg0 = args.getString(0);
		
		Lookup.lookup(arg0, sender);
	}
	
	@Command(aliases = {"a", "sch", "mb"}, desc = "Staff chat", usage = "<message>", min = 1, max = -1)
	public static void staffChat(final CommandContext args, CommandSender sender) throws Exception{
		String message = args.getJoinedStrings(0);
		
		StaffChat.sendMessage(message, sender);
	}
	
	@Command(aliases = {"report"}, desc = "Report a player", usage = "<player> <reason>", min = 2, max = -1)
	public static void reportPlayer(final CommandContext args, CommandSender sender) throws Exception{
		Player player = Bukkit.getPlayer(args.getString(0));
		String reason = args.getJoinedStrings(1);
		
		if (sender instanceof ConsoleCommandSender)
			sender.sendMessage(ChatColor.RED+"That command can only be run by a player!");
		else if (player == null)
			sender.sendMessage(ChatColor.RED+args.getString(0)+" is not online.");
		else
			Reports.submitReport(player, reason, sender);
	}
	
	/*@Command(aliases = {"addplayer"}, desc = "Manually add a player to the database", usage = "<username>", min = 1, max = 1)
	public static void addUser(final CommandContext args, CommandSender sender) throws Exception{
		String user = args.getString(0);
		
		//Players.addPlayer(user);
	}*/
	
	@Command(aliases = {"seen", "find"}, desc = "See which server a player is currently on, or where and when they were last online", usage = "<username>", min = 1, max = 1)
	public static void findUser(final CommandContext args, CommandSender sender) throws Exception{
		String user = args.getString(0);
		
		PlayerCommands.findPlayer(user, sender);
	}

}
