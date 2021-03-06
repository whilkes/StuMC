package eu.stumc.plugin;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class Commands {
	
	@Command(aliases = {"warn", "w"}, desc = "Warn a player", usage = "<player> <reason>",
			min = 2, max = -1)
	@CommandPermissions("stumc.mod")
	public static void warnCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Player player = Bukkit.getPlayer(args.getString(0));
		String reason = args.getJoinedStrings(1);
		if (player == null) {
			UUID uuid = DatabaseOperations.getUuidFromName(args.getString(0));
			if (uuid == null) {
				sender.sendMessage(ChatColor.RED + "Player does not exist.");
				return;
			}
			Punishments.warn(sender, Bukkit.getOfflinePlayer(uuid), reason, false);
		} else
			Punishments.warn(sender, player, reason, true);
	}
	
	//flag k = kick, flag t = 7 day ban
	@Command(aliases = {"punish", "p"}, desc = "Punish a player", usage = "<player> <reason>",
			flags = "kt", min = 2, max = -1)
	@CommandPermissions("stumc.mod")
	public static void punishCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Player player = Bukkit.getPlayer(args.getString(0));
		String reason = args.getJoinedStrings(1);
		int type = 0;
		if (args.hasFlag('k'))
			type = 1;
		else if (args.hasFlag('t'))
			type = 2;
		if (player == null) {
			UUID uuid = DatabaseOperations.getUuidFromName(args.getString(0));
			if (uuid == null) {
				sender.sendMessage(ChatColor.RED + "Player does not exist.");
				return;
			}
			Punishments.punish(sender, Bukkit.getOfflinePlayer(uuid), reason, false, type);
		} else
			Punishments.punish(sender, player, reason, true, type);
	}
	
	@Command(aliases = {"permaban", "pb"}, desc = "Permanently ban a player", usage = "<player> <reason>",
			min = 2, max = -1)
	@CommandPermissions("stumc.mod")
	public static void permaBanCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Player player = Bukkit.getPlayer(args.getString(0));
		String reason = args.getJoinedStrings(1);
		if (player == null) {
			UUID uuid = DatabaseOperations.getUuidFromName(args.getString(0));
			if (uuid == null) {
				sender.sendMessage(ChatColor.RED + "Player does not exist.");
				return;
			}
			Punishments.punish(sender, Bukkit.getOfflinePlayer(uuid), reason, false, 3);
		} else
			Punishments.punish(sender, player, reason, true, 3);
		
	}
	
	@Command(aliases = {"lookup", "l", "infractions"}, desc = "Look up a player's infractions", usage = "<player>",
			min = 1, max = 1)
	public static void lookupCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Player player = Bukkit.getPlayer(args.getString(0));
		if (player == null) {
			UUID uuid = DatabaseOperations.getUuidFromName(args.getString(0));
			if (uuid == null) {
				sender.sendMessage(ChatColor.RED + "Player does not exist.");
				return;
			}
			Actions.lookup(sender, Bukkit.getOfflinePlayer(uuid));
		} else
			Actions.lookup(sender, player);
	}
	
	@Command(aliases = "report", desc = "Report a player", usage = "<player> <reason>",
			min = 2, max = -1)
	public static void reportCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Player target = Bukkit.getPlayer(args.getString(0));
		String reason = args.getJoinedStrings(1);
		if (sender instanceof ConsoleCommandSender)
			sender.sendMessage(ChatColor.RED + "Command cannot be run from console.");
		else if (target == null)
			sender.sendMessage(ChatColor.RED + "Player does not exist.");
		else
			Actions.report(Bukkit.getPlayer(sender.getName()), target, reason);
	}
	
	@Command(aliases = "reports", desc = "View reports", usage = "[<page>]",
			min = 0, max = 1)
	@CommandPermissions("stumc.mod")
	public static void reportsCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		if (args.argsLength() == 0)
			Actions.getReports(sender, 0);
		else
			Actions.getReports(sender, args.getInteger(0));
	}
	
	@Command(aliases = {"sch", "a", "mb"}, desc = "Staff chat", usage = "<message>",
			flags = "g", min = 1, max = -1)
	@CommandPermissions("stumc.staff")
	public static void staffChatCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		String message = args.getJoinedStrings(0);
		boolean global = args.hasFlag('g');
		Actions.sendStaffMessage(sender, message, global);
	}
	
	@Command(aliases = "staff", desc = "See online staff")
	public static void staffCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Actions.getOnlineStaff(sender);
	}
	
	@Command(aliases = {"seen", "find"}, desc = "See where a player is or when they were last online",
			usage = "<player>", min = 1, max = 1)
	public static void seenCommand(final CommandContext args, CommandSender sender) throws CommandException, SQLException {
		Player player = Bukkit.getPlayer(args.getString(0));
		if (player == null) {
			UUID uuid = DatabaseOperations.getUuidFromName(args.getString(0));
			if (uuid == null)
				sender.sendMessage(ChatColor.RED + "Player does not exist.");
			else
				Actions.findPlayer(sender, Bukkit.getOfflinePlayer(uuid), false);
		} else
			Actions.findPlayer(sender, player, true);
	}
	
	@Command(aliases = {"spectate"}, desc = "Toggle spectator gamemode", min = 0, max = 0)
	@CommandPermissions("stumc.mod")
	public static void spectateCommand(final CommandContext args, CommandSender sender) throws CommandException {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "This command can only be run by players.");
			return;
		}
		Player player = Bukkit.getPlayer(sender.getName());
		if (player.getGameMode() == GameMode.SPECTATOR)
			player.setGameMode(GameMode.SURVIVAL);
		else
			player.setGameMode(GameMode.SPECTATOR);
	}
}
