package eu.stumc.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;

import eu.stumc.plugin.events.JoinEvent;
import eu.stumc.plugin.events.LoginEvent;
import eu.stumc.plugin.events.QuitEvent;

public class StuMC extends JavaPlugin {
	
	public static Connection conn;
	public static String serverName;
	//public ConcurrentHashMap<UUID, PlayerData> players;
    private CommandsManager<CommandSender> commands;
	
	@Override
	public void onEnable() {
		BukkitTask getPunish;
		BukkitTask getReports;
		saveDefaultConfig();
		openSqlConnection();
		setupSqlDatabase();
		setupCommands();
		serverName = getConfig().getString("stumc.server");
		//players = new ConcurrentHashMap<UUID, PlayerData>();
		getServer().getPluginManager().registerEvents(new LoginEvent(), this);
		getServer().getPluginManager().registerEvents(new JoinEvent(), this);
		getServer().getPluginManager().registerEvents(new QuitEvent(), this);
		if (getConfig().getBoolean("stumc.xserverpunishments"))
			getPunish = new eu.stumc.plugin.threads.PunishThread().runTaskTimer(this, 20L, 200L);
		if (getConfig().getBoolean("stumc.xserverreports"))
			getReports = new eu.stumc.plugin.threads.ReportsThread().runTaskTimer(this, 20L, 200L);
		getLogger().info("StuMC Plugin Enabled!");
	}
	
	@Override
	public void onDisable() {
		closeSqlConnection();
		getLogger().info("StuMC Plugin Disabled!");
	}
	
	public void openSqlConnection() {
		try {
			getSqlConnection();
		} catch (SQLException e) {
			getLogger().severe("Failed to establish database connection: "+e);
			e.printStackTrace();
		}
	}
	
	private void getSqlConnection() throws SQLException {
		String serverAddress = getConfig().getString("stumc.database.address");
		String serverPort = getConfig().getString("stumc.database.port");
		String database = getConfig().getString("stumc.database.database");
		String addr = "jdbc:mysql://" + serverAddress + ":" + serverPort + "/" + database;
		Properties connectionProps = new Properties();
		connectionProps.put("user", getConfig().getString("stumc.database.username"));
		connectionProps.put("password", getConfig().getString("stumc.database.password"));
		conn = DriverManager.getConnection(addr, connectionProps);
		getLogger().info("Connected to database");
	}
	
	private void setupSqlDatabase() {
		
	}
	
	public void closeSqlConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			getLogger().severe("Couldn't close connection: "+e);
			e.printStackTrace();
		}
	}
	
	private void setupCommands() {
		this.commands = new CommandsManager<CommandSender>() {
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
		};
		CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
		cmdRegister.register(Commands.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			this.commands.execute(cmd.getName(), args, sender, sender);
		} catch (CommandPermissionsException e) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
		} catch (MissingNestedCommandException e) {
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch (CommandUsageException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch (WrappedCommandException e) {
			if (e.getCause() instanceof NumberFormatException) {
				sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
			} else {
				sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
				e.printStackTrace();
			}
		} catch (CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}
	
}
