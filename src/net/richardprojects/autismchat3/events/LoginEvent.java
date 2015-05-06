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
		if(Config.redHidesLoginNotification) {
			String msg = e.getJoinMessage();
			e.setJoinMessage("");
			
			for(Player p : plugin.getServer().getOnlinePlayers()) {
				if(PlayerData.getPlayerColor(p.getUniqueId()) == Color.RED) {
					
				} else {
					p.sendMessage(msg);
				}
			}
		}
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				Player player = e.getPlayer();
				
				//Update the UUID's file every time a player joins
				plugin.updateUUID(player.getName(), player.getUniqueId());
				
				boolean playerJoinedBefore = true;
				
				//Handle New User creation
				if(!PlayerData.playerExist(player.getUniqueId())) {
					playerJoinedBefore = false;
					//Handle party creation
					if(Config.template_partyID == 0) {
						//Create new party
						int partyID = 0;
						try {
							partyID = PartyUtils.createParty(player.getName(), player.getUniqueId());
						} catch(Exception ex) {
							AutismChat3.log.info("An error occurred while creating a new party... Please check your file permissions.");
							ex.printStackTrace();
						}
						if(partyID != 0) PlayerData.newUser(player.getName(), player.getUniqueId(), partyID);
					} else {
						if(PartyUtils.partyExists(Config.template_partyID)) {
							PlayerData.newUser(player.getName(), player.getUniqueId(), Config.template_partyID);
							PartyUtils.joinParty(Config.template_partyID, player.getUniqueId());
						} else {
							int partyID = 0;
							try {
								partyID = PartyUtils.createParty(player.getName(), player.getUniqueId());
							} catch(Exception ex) {
								AutismChat3.log.info("An error occurred while creating a new party... Please check your file permissions.");
								ex.printStackTrace();
							}
							if(partyID != 0) PlayerData.newUser(player.getName(), player.getUniqueId(), partyID);
						}
					}
				}
		
				Color playerColor = PlayerData.getPlayerColor(player.getUniqueId());
				if(playerColor == Color.RED) {
					AutismChat3.redTeam.addPlayer(player);				
				} else if(playerColor == Color.BLUE) {
					AutismChat3.blueTeam.addPlayer(player);					
				} else if(playerColor == Color.GREEN) {
					AutismChat3.greenTeam.addPlayer(player);										
				} else if(playerColor == Color.YELLOW) {
					AutismChat3.yellowTeam.addPlayer(player);
				}
				player.setScoreboard(AutismChat3.board);
				
				//Show Message of the Day
				if(!(playerJoinedBefore && Config.firstJoin)) {
					for(String msg : Config.motd) {
						msg = Utils.colorCodes(msg);
						player.sendMessage(msg);
					}
				}
				
				//Show loginreport
				if(Config.loginReport) {
					//Loading Settings transition message
					player.sendMessage(Utils.colorCodes(Messages.message_loadingSettings));
					Color playersColor = PlayerData.getPlayerColor(player.getUniqueId());
					String msg = "&7Your colour setting: " + Color.colorCode(playersColor) + Color.toString(playersColor);
					player.sendMessage(Utils.colorCodes(msg));
					
					//Display Yellow List members
					player.sendMessage(Utils.colorCodes("&7The following players are on your Yellow List:"));
					msg = "";
					List<UUID> yellowListMembers = PlayerData.getYellowListMembers(player.getUniqueId());
					for(UUID member : yellowListMembers) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
							msg = msg + ", " + playerName;
						}
					}
					if(yellowListMembers.size() > 0) {
						msg = msg.substring(2);
						player.sendMessage(Utils.colorCodes(msg));
					}					
					//Global Chat
					if(PlayerData.globalChatEnabled(player.getUniqueId())) {
						msg = "&7Global chat will be displayed to you.";
					} else {
						msg = "&7Global chat will not be displayed to you.";
					}
					player.sendMessage(Utils.colorCodes(msg));
					
					//Party members
					int partyId = PlayerData.getPartyID(player.getUniqueId());
					if(partyId > 0) {
						List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
						List<UUID> onlineMembers = new ArrayList<UUID>();
						for(UUID member : partyMembers) {
							Player cPlayer = plugin.getServer().getPlayer(member);
							if(cPlayer != null) {
								onlineMembers.add(member);
							}
						}
						msg = "&7Players in party with you: {PARTYMEMBERS} &rof which {ONLINEPARTYMEMBERS} &rare online.";
						String partyMemberString = "";
						String onlineMemberString = "";
						//Create party list
						for(UUID member : partyMembers) {
							String playerName = plugin.getName(member);
							if(playerName != null) {
								playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
								partyMemberString = partyMemberString + ", " + playerName;
							}
						}
						partyMemberString = partyMemberString.substring(2);
						//Create list of players in the party who are online
						for(UUID member : onlineMembers) {
							String playerName = plugin.getName(member);
							if(playerName != null) {
								playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
								onlineMemberString = onlineMemberString + ", " + playerName;
							}
						}
						onlineMemberString = onlineMemberString.substring(2);
						msg = msg.replace("{PARTYMEMBERS}", partyMemberString);
						msg = msg.replace("{ONLINEPARTYMEMBERS}", onlineMemberString);
						player.sendMessage(Utils.colorCodes(msg));
					}
				}
			}
		});
	}
}
