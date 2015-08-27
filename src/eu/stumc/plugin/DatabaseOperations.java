package eu.stumc.plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.stumc.plugin.Data.PlayerData;
import eu.stumc.plugin.Data.PunishmentData;
import eu.stumc.plugin.Data.ReportData;

public class DatabaseOperations {
	
	public static UUID getUuidFromName(String name) throws SQLException {
		PreparedStatement query = null;
		String queryString = "SELECT * FROM stumc_users WHERE username = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, name);
		ResultSet result = query.executeQuery();
		while (result.next()) {
			return UUID.fromString(result.getString("uuid"));
		}
		return null;
	}

	public static PlayerData getPlayerDataByUuid(UUID uuid) throws SQLException {
		PreparedStatement query = null;
		PlayerData data = null;
		String queryString = "SELECT * FROM stumc_users WHERE uuid = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, uuid.toString());
		ResultSet result = query.executeQuery();
		while (result.next()) {
			data = new PlayerData(uuid, result.getString("username"),
					result.getString("last_ip"), result.getString("server"),
					Utils.intToBool(result.getInt("isOnline")),
					result.getInt("last_x"), result.getInt("last_y"),
					result.getInt("last_z"));
		}
		return data;
	}

	public static PlayerData getPlayerDataByName(String name)
			throws SQLException {
		PreparedStatement query = null;
		PlayerData data = null;
		String queryString = "SELECT * FROM stumc_users WHERE username = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, name);
		ResultSet result = query.executeQuery();
		while (result.next()) {
			data = new PlayerData(UUID.fromString(result.getString("uuid")),
					result.getString("username"), result.getString("last_ip"),
					result.getString("server"), Utils.intToBool(result
							.getInt("isOnline")), result.getInt("last_x"),
					result.getInt("last_y"), result.getInt("last_z"));
		}
		return data;
	}

	public static List<PunishmentData> getPunishmentsByUuid(UUID uuid)
			throws SQLException {
		PreparedStatement query = null;
		List<PunishmentData> punishments = new ArrayList<PunishmentData>();
		String queryString = "SELECT * FROM stumc_punishments WHERE active = 1 AND punished_uuid = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, uuid.toString());
		ResultSet result = query.executeQuery();
		while (result.next()) {
			punishments.add(new PunishmentData(result.getInt("id"), result
					.getString("type"), result.getString("reason"),
					UUID.fromString(result.getString("punisher_uuid")), 
					result.getLong("timestamp"), result.getLong("expiry"),
					result.getString("server")));
		}
		return punishments;
	}

	public static List<PunishmentData> getPunishmentsByName(String name)
			throws SQLException {
		PreparedStatement query = null;
		List<PunishmentData> punishments = new ArrayList<PunishmentData>();
		String queryString = "SELECT * FROM stumc_punishments WHERE punished_uuid = ?";
		String uuid = getPlayerDataByName(name).getUuid().toString();
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, uuid);
		ResultSet result = query.executeQuery();
		while (result.next()) {
			punishments.add(new PunishmentData(result.getInt("id"), result
					.getString("type"), result.getString("reason"),
					UUID.fromString(result.getString("punisher_uuid")), 
					result.getLong("timestamp"), result.getLong("expiry"),
					result.getString("server")));
		}
		return punishments;
	}

	public static void insertUserIntoDatabase(UUID uuid, String name,
			String ipAddress) throws SQLException {
		PreparedStatement query = null;
		String queryString = "INSERT INTO stumc_users (uuid, username, last_ip, server, isOnline, last_x, last_y, last_z, timestamp) "
				+ "VALUES (?, ?, ?, '" + StuMC.serverName + "', 1, 0, 0, 0, " + System.currentTimeMillis() / 1000 + ")";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, uuid.toString());
		query.setString(2, name);
		query.setString(3, ipAddress);
		query.executeUpdate();
	}

	public static void updateNameInDatabase(UUID uuid, String name)
			throws SQLException {
		PreparedStatement query = null;
		String queryString = "UPDATE stumc_users SET name = ? WHERE uuid = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, name);
		query.setString(2, uuid.toString());
		query.executeUpdate();
	}

	public static void setPlayerOnlineInDatabase(UUID uuid, String ipAddress)
			throws SQLException {
		PreparedStatement query = null;
		String queryString = "UPDATE stumc_users SET isOnline = 1, server = '"
				+ StuMC.serverName + "', last_ip = ?, timestamp = ? WHERE uuid = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, ipAddress);
		query.setLong(2, System.currentTimeMillis() / 1000);
		query.setString(3, uuid.toString());
		query.executeUpdate();
	}

	public static void setPlayerOfflineInDatabase(UUID uuid, int last_x,
			int last_y, int last_z) throws SQLException {
		PreparedStatement query = null;
		String queryString = "UPDATE stumc_users SET isOnline = 0, server = '"
				+ StuMC.serverName
				+ "', last_x = ?, last_z = ?, last_y = ?, timestamp = ? WHERE uuid = ?";
		query = StuMC.conn.prepareStatement(queryString);
		query.setInt(1, last_x);
		query.setInt(2, last_y);
		query.setInt(3, last_z);
		query.setLong(4, System.currentTimeMillis() / 1000);
		query.setString(5, uuid.toString());
		query.executeUpdate();
	}

	public static void insertPunishment(UUID uuid, UUID punishedUuid, String type, String reason,
			long timestamp, long expiry) throws SQLException {
		PreparedStatement query = null;
		String queryString = "INSERT INTO stumc_punishments (punisher_uuid, punished_uuid, "
				+ "type, reason, timestamp, expiry, active, server) "
				+ "VALUES (?, ?, ?, ?, ?, ?, '1', '" + StuMC.serverName + "')";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, uuid.toString());
		query.setString(2, punishedUuid.toString());
		query.setString(3, type);
		query.setString(4, reason);
		query.setLong(5, timestamp);
		query.setLong(6, expiry);
		query.executeUpdate();
	}

	public static void insertReport(UUID uuid, UUID target, String reason,
			long timestamp) throws SQLException {
		PreparedStatement query = null;
		String queryString = "INSERT INTO stumc_reports (reporter_uuid, reported_uuid, "
				+ "reason, timestamp, server) "
				+ "VALUES (?, ?, ?, ?, '" + StuMC.serverName + "')";
		query = StuMC.conn.prepareStatement(queryString);
		query.setString(1, uuid.toString());
		query.setString(2, target.toString());
		query.setString(3, reason);
		query.setLong(4, timestamp);
		query.executeUpdate();
	}

	public static List<ReportData> getReports(int page) throws SQLException {
		PreparedStatement query = null;
		List<ReportData> reports = new ArrayList<ReportData>();
		int start = 0;
		if (page > 0)
			start = (page - 1) * 15;
		String queryString = "SELECT * FROM stumc_reports ORDER BY timestamp DESC LIMIT " + start + ", 15";
		query = StuMC.conn.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while (result.next()) {
			reports.add(new ReportData(UUID.fromString(result.getString("reporter_uuid")),
					UUID.fromString(result.getString("reported_uuid")), result.getString("reason"),
					result.getLong("timestamp"), result.getString("server")));
		}
		return reports;
	}

}
