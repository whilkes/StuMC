package eu.stumc.plugin.threads;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.stumc.plugin.Punishments;
import eu.stumc.plugin.StuMC;

public class PunishThread extends BukkitRunnable {
	
	private boolean first = true;
	private int lastId = 0;

	@Override
	public void run() {
		try {
			if (first) {
				PreparedStatement query = StuMC.conn.prepareStatement(
						"SELECT MAX(id) FROM stumc_punishments");
				ResultSet result = query.executeQuery();
				while (result.next()) {
					lastId = result.getInt(1);
				}
				first = false;
			}
			
			PreparedStatement query = StuMC.conn.prepareStatement(
					"SELECT * FROM stumc_punishments WHERE id > ?");
			query.setInt(1, lastId);
			ResultSet result = query.executeQuery();
			while (result.next()) {
				UUID punisher = UUID.fromString(result.getString("punisher_uuid"));
				UUID punished = UUID.fromString(result.getString("punished_uuid"));
				String reason = result.getString("reason");
				String server = result.getString("server");
				String type = result.getString("type");
				long expiry = result.getLong("expiry") * 1000;
				lastId = result.getInt("id");
				
				Punishments.punishFromOtherServer(punisher, punished, reason, server, type, expiry);
			}
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Error occurred executing query: " + e);
			e.printStackTrace();
		}
	}

}
