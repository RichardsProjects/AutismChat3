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

import java.util.List;
import java.util.UUID;

import net.richardprojects.autismchat3.ACPlayer;
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
		UUID uuid = player.getUniqueId();
		
		// Update the UUID's file every time a player joins
		plugin.updateUUID(player.getName(), player.getUniqueId());

		boolean playerJoinedBefore = true;

		// handle new user creation
		if (plugin.getACPlayer(player.getUniqueId()) == null) {
			playerJoinedBefore = false;
			// handle party creation
			if (Config.template_partyID == 0) {
				// create new party
				int partyID = 0;
				try {
					partyID = PartyUtils.createParty(player.getName(), uuid);
				} catch (Exception ex) {
					String err = "An error occurred while creating a new party... ";
					AutismChat3.log.info(err + " Please check your file permissions.");
					ex.printStackTrace();
				}
				if (partyID != 0)
					plugin.createNewPlayer(uuid, partyID);
			} else {
				if (PartyUtils.partyExists(Config.template_partyID)) {
					plugin.createNewPlayer(uuid, Config.template_partyID);
					PartyUtils.joinParty(plugin, Config.template_partyID, player.getUniqueId());
				} else {
					int partyID = 0;
					try {
						partyID = PartyUtils.createParty(player.getName(),
								player.getUniqueId());
					} catch (Exception ex) {
						String err = "An error occurred while creating a new party... ";
						AutismChat3.log.info(err + " Please check your file permissions.");
						ex.printStackTrace();
					}
					if (partyID != 0)
						plugin.createNewPlayer(uuid, partyID);
				}
			}
		}

		ACPlayer acPlayer = plugin.getACPlayer(uuid);
		Color pColor = acPlayer.getColor();
		if (pColor == Color.RED) {
			AutismChat3.redTeam.addPlayer(player);
		} else if (pColor == Color.BLUE) {
			AutismChat3.blueTeam.addPlayer(player);
		} else if (pColor == Color.GREEN) {
			AutismChat3.greenTeam.addPlayer(player);
		} else if (pColor == Color.YELLOW) {
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
			String msg = Utils.colorCodes(Messages.message_loadingSettings);
			player.sendMessage(msg);

			// Send Statuses for login report
			String[] loginReport = Config.loginReportStatuses.split(",");
			for (int i = 0; i < loginReport.length; i++) {
				Utils.sendStatus(loginReport[i], player.getUniqueId(), plugin);
			}
		}
		
		
		for(Player cPlayer : plugin.getServer().getOnlinePlayers()) {
			boolean displayMessage = true;
			
			if(Config.redHidesLoginNotification) {
				if(pColor == Color.RED) {
					displayMessage = false;
				}
			}
			
			if(displayMessage) {

				if(cPlayer.getUniqueId().equals(e.getPlayer().getUniqueId())) {
					String msg = Messages.message_joinMessage;
					String name = Utils.formatName(plugin, player.getUniqueId(), null);
					msg = msg.replace("{PLAYER}", name);
					cPlayer.sendMessage(Utils.colorCodes(msg));
				} else {
					int cPlayerPartyId = plugin.getACPlayer(cPlayer.getUniqueId()).getPartyId();
					String name = Utils.formatName(plugin, player.getUniqueId(), cPlayer.getUniqueId());
					if(cPlayerPartyId > 0) {
						List<UUID> partyMembers = PartyUtils.partyMembers(cPlayerPartyId);
						if(partyMembers.contains(e.getPlayer().getUniqueId())) {
							String msg = Messages.message_joinMessageParty;
							msg = msg.replace("{PLAYER}", name);
							cPlayer.sendMessage(Utils.colorCodes(msg));
						} else {
							String msg = Messages.message_joinMessage;
							msg = msg.replace("{PLAYER}", name);
							cPlayer.sendMessage(Utils.colorCodes(msg));
						}
					} else {
						String msg = Messages.message_joinMessage;
						msg = msg.replace("{PLAYER}", name);
						cPlayer.sendMessage(Utils.colorCodes(msg));
					}
				}
			}
		}
	}
}
