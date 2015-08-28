package eu.stumc.plugin.events;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import eu.stumc.plugin.DatabaseOperations;
import eu.stumc.plugin.Strings;
import eu.stumc.plugin.data.PlayerData;
import eu.stumc.plugin.data.PunishmentData;

public class LoginEvent implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		try {
			UUID uuid = event.getUniqueId();
			PlayerData playerData = DatabaseOperations.getPlayerDataByUuid(uuid);
			if (playerData == null)
				DatabaseOperations.insertUserIntoDatabase(uuid,
						event.getName(), event.getAddress().toString());
			else if (!playerData.getName().equals(event.getName()))
				DatabaseOperations.updateNameInDatabase(uuid, event.getName());

			List<PunishmentData> punishments = DatabaseOperations.getPunishmentsByUuid(uuid);
			for (PunishmentData punishmentData : punishments) {
				if (returnBanType(punishmentData) == 2) {
					String msg = Strings.PERMABANNED
						.replace("$1", punishmentData.getReason());
					event.disallow(Result.KICK_BANNED, msg);
				} else if (returnBanType(punishmentData) == 1) {
					String msg = Strings.TEMPBANNED
						.replace("$1", punishmentData.getReason())
						.replace("$2", new Date(punishmentData.getExpiry() * 1000).toString());
					event.disallow(Result.KICK_BANNED, msg);
				}
			}
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Failed to query database: "+e);
			e.printStackTrace();
			event.disallow(Result.KICK_OTHER, Strings.CONNECT_FAIL_DATABASE_ERROR);
		}
	}

	/*
	 * ban type 0 = not banned 1 = tempban 2 = permaban
	 */
	private int returnBanType(PunishmentData data) {
		if (data.getType().equals("ban") && data.getExpiry() == 0)
			return 2;
		else if ((data.getType().equals("ban") || data.getType().equals(
				"tempban"))
				&& data.getExpiry() * 1000 > System.currentTimeMillis())
			return 1;
		return 0;
	}

}
