package eu.stumc.plugin;

import org.bukkit.ChatColor;

public class Strings {
	/*
	 * For the below strings, $x should be replaced with the relevant text.
	 * E.g. If $1 is the punisher, $2 is the punished, and $3 is the reason, it will
	 * be listed as follows:
	 * punisher, punished, reason
	 */
	
	//Strings for disconnecting players.
	//reason
	public static final String KICKED = ChatColor.translateAlternateColorCodes('&',
			"&6Kicked &e-> &b$1\n\n"
			+ "&6Disagree with your punishment?\n"
			+ "Appeal it at &chttps://www.stumc.eu/support/");
	//reason, expiry
	public static final String TEMPBANNED = ChatColor.translateAlternateColorCodes('&',
			"&cTemporarily banned &e-> &b$1\n"
			+ "&6Expires &c$2\n\n"
			+ "&6Disagree with your punishment?\n"
			+ "Appeal it at &chttps://www.stumc.eu/support/");
	//reason
	public static final String PERMABANNED = ChatColor.translateAlternateColorCodes('&',
			"&cPermanently banned &e-> &b$1\n\n"
			+ "&6Disagree with your punishment?\n"
			+ "Appeal it at &chttps://www.stumc.eu/support/");
	public static final String WARNED_TITLE = ChatColor.translateAlternateColorCodes('&',
			"&6&k~ ~ ~ ~&r&6&l Warning &k~ ~ ~ ~");
	//reason
	public static final String WARNED_1 = ChatColor.translateAlternateColorCodes('&',
			"&c$1");
	public static final String WARNED_2 = ChatColor.translateAlternateColorCodes('&',
			"&a$1");
	public static final String WARNED_3 = ChatColor.translateAlternateColorCodes('&',
			"&9$1");
	
	//Broadcasts to chat when punishment/warning issued
	//punisher, type, punished, reason
	public static final String PUNISHMENT_BROADCAST = ChatColor.translateAlternateColorCodes('&',
			"&3$1 &e-> &6$2 &e-> &3$3 &e-> &6$4");
	//punisher, punished, reason
	public static final String WARN_BROADCAST = ChatColor.translateAlternateColorCodes('&',
			"&6[&cS&6] &3$1 &6warned &3$2&6: &f$3");
	//punisher, type, punished, reason, server
	public static final String PUNISHMENT_BROADCAST_OTHER_SERVER = ChatColor.translateAlternateColorCodes('&',
			"&6[&c$5&6] &3$1 &e-> &6$2 &e-> &3$3 &e-> &6$4");
	//punisher, punished, reason, server
	public static final String WARN_BROADCAST_OTHER_SERVER = ChatColor.translateAlternateColorCodes('&',
			"&6[&cS&6] &6[&c$4&6] &3$1 &6warned &3$2&6: &f$3");
	
	//reporter, reported, reason
	public static final String REPORT_BROADCAST = ChatColor.translateAlternateColorCodes('&',
			"&6[&cR&6] &3$1 &6reported &3$2&6: &f$3");
	//reporter, reported, reason, server
	public static final String REPORT_BROADCAST_OTHER_SERVER = ChatColor.translateAlternateColorCodes('&',
			"&6[&cR&6] &6[&c$4&6] &3$1 &6reported &3$2&6: &f$3");
	public static final String REPORT_SUBMITTED = ChatColor.translateAlternateColorCodes('&',
			"&bThank you. Your report has been submitted.");
	
	//user, server, date
	public static final String SEEN_COMMAND_OFFLINE = ChatColor.translateAlternateColorCodes('&',
			"&3$1 &6is currently &coffline&6, last seen on &c$2&6, on &c$3&6.");
	public static final String SEEN_COMMAND_OTHER = ChatColor.translateAlternateColorCodes('&',
			"&3$1 &6is currently &aonline&6 on &c$2&6.");
	public static final String SEEN_COMMAND_CURRENT = ChatColor.translateAlternateColorCodes('&',
			"&3$1 &6is currently &aonline&6.");
	public static final String SEEN_COMMAND_IP = ChatColor.translateAlternateColorCodes('&',
			"&6IP address: &f$1");
	//world, x, y, z
	public static final String SEEN_COMMAND_POSITION = ChatColor.translateAlternateColorCodes('&',
			"&6Position: &f($1, $2, $3, $4)");
	
	//punisher, type, reason
	public static final String LIST_PUNISHMENT = ChatColor.translateAlternateColorCodes('&',
			"&3$1 &6-> &3$2 &6-> &c$3");
	//player
	public static final String NO_PUNISHMENTS_HEADER = ChatColor.translateAlternateColorCodes('&',
			"&3$1 &ehas no punishments.");
	public static final String PUNISHMENTS_HEADER = ChatColor.translateAlternateColorCodes('&',
			"&b- - - - &3$1&6's punishments &b- - - -");
	
	public static final String REPORTS_HEADER = ChatColor.translateAlternateColorCodes('&',
			"&b- - - - &6Reports &b- - - -");
	//number, server, reporter, reported, reason
	public static final String LIST_REPORT = ChatColor.translateAlternateColorCodes('&',
			"&b[$1] [$2] &3$3 &6reported &3$4&6: &f$5");
	
	//user, message
	public static final String STAFF_CHAT = ChatColor.translateAlternateColorCodes('&',
			"&6[&cS&6] &3$1&6: &f$2");
	
	public static final String CONNECT_FAIL_DATABASE_ERROR = ChatColor.RED +
			"Failed to connect to the server: A database error occurred.\n"
			+ "For assistance, please contact https://www.stumc.eu/support/";
	
	public static final String STAFF_LIST_HEADER = ChatColor.translateAlternateColorCodes('&',
			"&b- - - - &6Online staff &b- - - -");
	public static final String STAFF_LIST_SERVER = ChatColor.translateAlternateColorCodes('&',
			"&6[&c$1&6] ");
	
}
