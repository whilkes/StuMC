package eu.stumc.plugin.threads;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.stumc.plugin.Actions;
import eu.stumc.plugin.StuMC;

public class ReportsThread extends BukkitRunnable {
	
	private boolean first = true;
	private int lastId = 0;

	@Override
	public void run() {
		try {
			if (first) {
				PreparedStatement query = StuMC.conn.prepareStatement(
						"SELECT MAX(id) FROM stumc_reports");
				ResultSet result = query.executeQuery();
				while (result.next()) {
					lastId = result.getInt(1);
				}
				first = false;
			}
			
			PreparedStatement query = StuMC.conn.prepareStatement(
					"SELECT * FROM stumc_reports WHERE id > ? AND server != ?");
			query.setInt(1, lastId);
			query.setString(2, StuMC.serverName);
			ResultSet result = query.executeQuery();
			while (result.next()) {
				UUID reporter = UUID.fromString(result.getString("reporter_uuid"));
				UUID reported = UUID.fromString(result.getString("reported_uuid"));
				String reason = result.getString("reason");
				String server = result.getString("server");
				lastId = result.getInt("id");
				
				Actions.broadcastReportFromOtherServer(reporter, reported, reason, server);
			}
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Error occurred executing query: " + e);
			e.printStackTrace();
		}
	}
	
}
