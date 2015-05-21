/*   This file is part of AutismChat3.
 *
 *    AutismChat3 is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License.
 *
 *    You can view a copy of the GNU General Public License below
 *    http://www.gnu.org/licenses/
 */

package net.richardprojects.autismchat3.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Config;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PartyUtils;
import net.richardprojects.autismchat3.PlayerData;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginEvent implements Listener {

	private AutismChat3 plugin;

	public LoginEvent(AutismChat3 plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void loginEvent(final PlayerJoinEvent e) {
		e.setJoinMessage("");
		Player player = e.getPlayer();
		
		// Update the UUID's file every time a player joins
		plugin.updateUUID(player.getName(), player.getUniqueId());

		boolean playerJoinedBefore = true;

		// Handle New User creation
		if (!PlayerData.playerExist(player.getUniqueId())) {
			playerJoinedBefore = false;
			// Handle party creation
			if (Config.template_partyID == 0) {
				// Create new party
				int partyID = 0;
				try {
					partyID = PartyUtils.createParty(player.getName(),
							player.getUniqueId());
				} catch (Exception ex) {
					AutismChat3.log
							.info("An error occurred while creating a new party... Please check your file permissions.");
					ex.printStackTrace();
				}
				if (partyID != 0)
					PlayerData.newUser(player.getName(), player.getUniqueId(),
							partyID);
			} else {
				if (PartyUtils.partyExists(Config.template_partyID)) {
					PlayerData.newUser(player.getName(), player.getUniqueId(),
							Config.template_partyID);
					PartyUtils.joinParty(Config.template_partyID,
							player.getUniqueId());
				} else {
					int partyID = 0;
					try {
						partyID = PartyUtils.createParty(player.getName(),
								player.getUniqueId());
					} catch (Exception ex) {
						AutismChat3.log
								.info("An error occurred while creating a new party... Please check your file permissions.");
						ex.printStackTrace();
					}
					if (partyID != 0)
						PlayerData.newUser(player.getName(),
								player.getUniqueId(), partyID);
				}
			}
		}

		Color playerColor = PlayerData.getPlayerColor(player.getUniqueId());
		if (playerColor == Color.RED) {
			AutismChat3.redTeam.addPlayer(player);
		} else if (playerColor == Color.BLUE) {
			AutismChat3.blueTeam.addPlayer(player);
		} else if (playerColor == Color.GREEN) {
			AutismChat3.greenTeam.addPlayer(player);
		} else if (playerColor == Color.YELLOW) {
			AutismChat3.yellowTeam.addPlayer(player);
		}
		player.setScoreboard(AutismChat3.board);

		// Show Message of the Day
		if (!(playerJoinedBefore && Config.firstJoin)) {
			for (String msg : Messages.motd) {
				msg = Utils.colorCodes(msg);
				player.sendMessage(msg);
			}
		}

		// Show loginreport
		if (Config.loginReport) {
			// Loading Settings transition message
			player.sendMessage(Utils
					.colorCodes(Messages.message_loadingSettings));

			// Send Statuses for login report
			String[] loginReport = Config.loginReportStatuses.split(",");
			for (int i = 0; i < loginReport.length; i++) {
				Utils.sendStatus(loginReport[i], player.getUniqueId(), plugin);
			}
		}
		
		//Send join message to all players
		String playerName = Color.colorCode(PlayerData.getPlayerColor(e.getPlayer().getUniqueId())) + player.getName();
		
		for(Player cPlayer : plugin.getServer().getOnlinePlayers()) {
			boolean displayMessage = true;
			
			if(Config.redHidesLoginNotification) {
				if(PlayerData.getPlayerColor(cPlayer.getUniqueId()) == Color.RED) {
					displayMessage = false;
				}
			}
			
			if(displayMessage) {

				if(cPlayer.getUniqueId().equals(e.getPlayer().getUniqueId())) {
					String msg = Messages.message_joinMessage;
					msg = msg.replace("{PLAYER}", playerName);
					cPlayer.sendMessage(Utils.colorCodes(msg));
				} else {
					int cPlayerPartyId = PlayerData.getPartyID(cPlayer.getUniqueId());
					if(cPlayerPartyId > 0) {
						List<UUID> partyMembers = PartyUtils.partyMembers(cPlayerPartyId);
						if(partyMembers.contains(e.getPlayer().getUniqueId())) {
							String msg = Messages.message_joinMessageParty;
							msg = msg.replace("{PLAYER}", playerName);
							cPlayer.sendMessage(Utils.colorCodes(msg));
						} else {
							String msg = Messages.message_joinMessage;
							msg = msg.replace("{PLAYER}", playerName);
							cPlayer.sendMessage(Utils.colorCodes(msg));
						}
					} else {
						String msg = Messages.message_joinMessage;
						msg = msg.replace("{PLAYER}", playerName);
						cPlayer.sendMessage(Utils.colorCodes(msg));
					}
				}
			}
		}
	}
}
