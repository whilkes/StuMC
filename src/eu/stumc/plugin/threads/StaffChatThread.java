package eu.stumc.plugin.threads;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.minecraft.util.commands.ChatColor;

import eu.stumc.plugin.Actions;
import eu.stumc.plugin.DatabaseOperations;
import eu.stumc.plugin.Strings;
import eu.stumc.plugin.StuMC;

public class StaffChatThread extends BukkitRunnable {
	
	private boolean first = true;
	private int lastId = 0;

	@Override
	public void run() {
		try {
			if (first) {
				PreparedStatement query = StuMC.conn.prepareStatement(
						"SELECT MAX(id) FROM stumc_staffchat");
				ResultSet result = query.executeQuery();
				while (result.next()) {
					lastId = result.getInt(1);
				}
				first = false;
			}
			
			PreparedStatement query = StuMC.conn.prepareStatement(
					"SELECT * FROM stumc_staffchat WHERE id > ? AND server != ?");
			query.setInt(1, lastId);
			query.setString(2, StuMC.serverName);
			ResultSet result = query.executeQuery();
			while (result.next()) {
				String senderName = "";
				UUID sender = UUID.fromString(result.getString("uuid"));
				String message = result.getString("message");
				String server = result.getString("server");
				lastId = result.getInt("id");
				
				if (sender.toString().equals("00000000-0000-0000-0000-000000000000"))
					senderName = ChatColor.GOLD + "(console)";
				else if (Bukkit.getPlayer(sender) != null)
					senderName = Bukkit.getPlayer(sender).getDisplayName();
				else
					senderName = DatabaseOperations.getNameFromUuid(sender);
				
				Actions.sendStaffMessage(Strings.STAFF_CHAT_OTHER_SERVER
						.replace("$1", senderName)
						.replace("$2", message)
						.replace("$3", server));
			}
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Error occurred executing query: " + e);
			e.printStackTrace();
		}
	}
}