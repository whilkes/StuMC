package eu.stumc.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lib.PatPeter.SQLibrary.MySQL;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;

public class StuMC extends JavaPlugin{
	
	public MySQL dbconn;
	//public ConcurrentHashMap<UUID, BanData> bans = new ConcurrentHashMap<UUID, BanData>();
	//public ConcurrentHashMap<UUID, PlayerData> players = new ConcurrentHashMap<UUID, PlayerData>();
    private CommandsManager<CommandSender> commands;
	
	@Override
	public void onEnable(){
		saveDefaultConfig();
		sqlConnection();
		sqlTableCheck();
		setupCommands();
		getLogger().info("StuMC Plugin Enabled");
	}
	
	@Override
	public void onDisable(){
		dbconn.close();
		getLogger().info("StuMC Plugin Disabled");
	}
	
	public void sqlConnection(){
		dbconn = new MySQL(
				getLogger(),
				"stumc_",
				getConfig().getString("database.server"),
				getConfig().getInt("database.port"),
				getConfig().getString("database.database"),
				getConfig().getString("database.username"),
				getConfig().getString("database.password")
				);
		try{
			dbconn.open();
		}catch (Exception e){
			getLogger().severe("Failed to connect to database: "+e.getMessage());
			e.printStackTrace();
			getPluginLoader().disablePlugin(this);
		}
	}
	
	public void sqlTableCheck(){
		if (dbconn.checkConnection()){
			if (!dbconn.checkTable("users"))
				createTable("users");
			if (!dbconn.checkTable("punishments"))
				createTable("punishments");
			if (!dbconn.checkTable("reports"))
				createTable("reports");
		}
	}
	
	public void createTable(String tableName){
		switch (tableName){
		case "users":
			insertQuery("CREATE TABLE `users` ("
					+ "`UUID` VARCHAR(36) NOT NULL,"
					+ "`last_username` VARCHAR(16) NOT NULL,"
					+ "`last_ip` VARCHAR(15) NOT NULL,"
					+ "`last_x` INT NULL,"
					+ "`last_y` INT NULL,"
					+ "`last_z` INT NULL,"
					+ "`last_timestamp` INT NULL,"
					+ "`server` VARCHAR(45) NOT NULL,"
					+ "`online` INT NULL,"
					+ "PRIMARY KEY (`UUID`));");
			insertQuery("INSERT INTO users (UUID, last_username, last_ip, server) "
					+ "VALUES (`(console)`,`(console)`,`0.0.0.0`,`(console)`);");
			break;
		case "punishments":
			insertQuery("CREATE TABLE `punishments` ("
					+ "`id` INT NOT NULL AUTO_INCREMENT,"
					+ "`punished_UUID` VARCHAR(36) NOT NULL,"
					+ "`punisher_UUID` VARCHAR(36) NOT NULL,"
					+ "`reason` VARCHAR(255) NOT NULL,"
					+ "`type` VARCHAR(45) NOT NULL,"
					+ "`time` INT NOT NULL,"
					+ "`expiry` INT NULL,"
					+ "`active` INT NOT NULL DEFAULT 1,"
					+ "`contested` INT NULL,"
					+ "`server` VARCHAR(45) NOT NULL,"
					+ "PRIMARY KEY (`id`),"
					+ "INDEX `fk_punished_UUID_idx` (`punished_UUID` ASC),"
					+ "INDEX `fk_punisher_UUID_idx` (`punisher_UUID` ASC),"
					+ "CONSTRAINT `fk_punished_UUID`"
					+ "FOREIGN KEY (`punished_UUID`)"
					+ "REFERENCES `users` (`UUID`)"
					+ "ON DELETE NO ACTION"
					+ "ON UPDATE NO ACTION,"
					+ "CONSTRAINT `fk_punisher_UUID`"
					+ "FOREIGN KEY (`punisher_UUID`)"
					+ "REFERENCES `users` (`UUID`)"
					+ "ON DELETE NO ACTION"
					+ "ON UPDATE NO ACTION);"); break;
		case "reports":
			insertQuery("CREATE TABLE `reports` ("
					+ "`id` INT NOT NULL AUTO_INCREMENT,"
					+ "`reporter_UUID` VARCHAR(36) NOT NULL,"
					+ "`reported_UUID` VARCHAR(36) NOT NULL,"
					+ "`reason` VARCHAR(255) NOT NULL,"
					+ "`time` INT NOT NULL,"
					+ "`server` VARCHAR(45) NOT NULL,"
					+ "PRIMARY KEY (`id`),"
					+ "INDEX `fk_reporter_UUID_idx` (`reporter_UUID` ASC),"
					+ "INDEX `fk_reported_UUID_idx` (`reported_UUID` ASC),"
					+ "CONSTRAINT `fk_reporter_UUID`"
					+ "FOREIGN KEY (`reporter_UUID`)"
					+ "REFERENCES `users` (`UUID`)"
					+ "ON DELETE NO ACTION"
					+ "ON UPDATE NO ACTION,"
					+ "CONSTRAINT `fk_reported_UUID`"
					+ "FOREIGN KEY (`reported_UUID`)"
					+ "REFERENCES `users` (`UUID`)"
					+ "ON DELETE NO ACTION"
					+ "ON UPDATE NO ACTION);"); break;
		default:
			try{
				throw new RuntimeException("Unknown table: "+tableName);
			}catch (RuntimeException e){
				getLogger().severe(e.getMessage());
				e.printStackTrace();
			}
			break;
		}	
	}
	
	public void insertQuery(String query){
		try{
			dbconn.insert(dbconn.prepare(query));
		}catch (SQLException e){
			getLogger().severe("The following query could not be run: "+query);
			getLogger().severe("The following exception occurred: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public ResultSet selectQuery(String query){
		try{
			return dbconn.query(dbconn.prepare(query));
		}catch (SQLException e){
			getLogger().severe("The following query could not be run: "+query);
			getLogger().severe("The following exception occurred: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
    private void setupCommands() {
    	this.commands = new CommandsManager<CommandSender>() {
    		@Override
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
	
    /*public void addPlayer(PlayerData data){
    	
    }*/
    
    public String getServerName(){
    	return getConfig().getString("database.servername");
    }

}
