/*   This file is part of AutismChat3.
*
*    AutismChat3 is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License.
*
*    You can view a copy of the GNU General Public License below
*    http://www.gnu.org/licenses/
*/

package net.richardprojects.autismchat3.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.md_5.bungee.api.ChatColor;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PartyUtils;
import net.richardprojects.autismchat3.PlayerData;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class YellowCommand implements CommandExecutor {
	
	private AutismChat3 plugin;
	
	public YellowCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player)
		{
			final Player player = (Player) sender;
			if(args.length == 0)
			{
				PlayerData.setColor(player.getUniqueId(), Color.YELLOW);
				//Replace the Yellow list variable with the names of people on their yellow list
				String yellowList = "";
				for(UUID uuid : PlayerData.getYellowListMembers(player.getUniqueId())) {
					String yellowName = plugin.getName(uuid);
					if(yellowName != null) {
						if(yellowList.equals("")) {
							yellowList = yellowName;
						} else {
							yellowList = yellowList + ", " + yellowName;
						}
					}
				}
				
				if(yellowList.length() == 0) {
					yellowList = "NONE";
				}
				
				String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_setYellow);
				msg = msg.replace("{yellow_list}", yellowList);
				player.sendMessage(msg);
				Team playerTeam = AutismChat3.board.getPlayerTeam(player);
				if(playerTeam != null) {
					String name = playerTeam.getName();
					if(name.equals("greenTeam")) {
						AutismChat3.greenTeam.removePlayer(player);
					} else if(name.equals("yellowTeam")) {
						AutismChat3.yellowTeam.removePlayer(player);
					} else if(name.equals("redTeam")) {
						AutismChat3.redTeam.removePlayer(player);
					} else if(name.equals("blueTeam")) {
						AutismChat3.blueTeam.removePlayer(player);
					}
				}
				AutismChat3.yellowTeam.addPlayer(player);
				for(Player cPlayer : plugin.getServer().getOnlinePlayers()) {
					cPlayer.setScoreboard(AutismChat3.board);
				}
				
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

					public void run() {
						//Check if there are people in the party who are not yellow
						boolean stayInParty = true;
						int currentPartyId = PlayerData.getPartyID(player.getUniqueId());
						List<UUID> currentPartyMemberlist = null;
						if(currentPartyId > 0) {
							currentPartyMemberlist = PartyUtils.partyMembers(currentPartyId);
							List<UUID> yellowMemberlist = PlayerData.getYellowListMembers(player.getUniqueId());
							if(currentPartyMemberlist.size() > 1) {
								for(UUID member : currentPartyMemberlist) {
									if(!yellowMemberlist.contains(member) && !member.equals(player.getUniqueId())) {
										stayInParty = false;
									}
								}
							}
						}
						
						if(!stayInParty) {
							try {
								//Message everyone
								for(UUID member : currentPartyMemberlist) {
									if(!member.equals(player.getUniqueId())) {
										Player cPlayer = plugin.getServer().getPlayer(member);
										if(cPlayer != null) {
											String msg2 = Messages.message_leaveParty;
											msg2 = msg2.replace("{PLAYER}", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
											msg2 = msg2.replace(" {PLAYERS} {REASON}", Messages.reasonLeaveYellow);
											cPlayer.sendMessage(Utils.colorCodes(msg2));
										}
									} else {
										String partyMemberlist = "";
										for(UUID playerUUID : currentPartyMemberlist) {
											partyMemberlist = partyMemberlist + ", " + plugin.getName(playerUUID);
										}
										partyMemberlist = partyMemberlist.substring(2);
										
										String msg2 = Messages.message_youLeaveParty;
										msg2 = msg2.replace("has", "have");
										msg2 = msg2.replace("{PLAYERS}", partyMemberlist);
										msg2 = msg2.replace("{REASON}", Messages.reasonYouYellow);
										player.sendMessage(Utils.colorCodes(msg2));
									}
								}
								
								//Remove player from old party
								PartyUtils.removePlayerParty(currentPartyId, player.getUniqueId());
								
								//Create a new party
								int newPartyId = PartyUtils.createParty(player.getName(), player.getUniqueId());
								
								//Update party id
								File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + player.getUniqueId().toString() + ".xml");
								DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
								DocumentBuilder docBuilder = docFactory.newDocumentBuilder();			
								Document doc = docBuilder.parse(xml);
								
								NodeList nList = doc.getElementsByTagName("party");
								Node tempNode = nList.item(0);
								tempNode.setTextContent(newPartyId + "");
								
								//Save
								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								DOMSource source = new DOMSource(doc);
								StreamResult result = new StreamResult(xml);
								transformer.setOutputProperty(OutputKeys.INDENT, "no");
								transformer.transform(source, result);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
				
				return true;
			} else {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("list")) {
						//Replace the Yellow list variable with the names of people on their yellow list
						String yellowList = "";
						for(UUID uuid : PlayerData.getYellowListMembers(player.getUniqueId())) {
							String yellowName = plugin.getName(uuid);
							if(yellowName != null) {
								if(yellowList.equals("")) {
									yellowList = yellowName;
								} else {
									yellowList = yellowList + ", " + yellowName;
								}
							}
						}
						String msg = Messages.prefix_Good + "Yellow List Members: " + yellowList;
						player.sendMessage(Utils.colorCodes(msg));
						return true;
					} else {
						sender.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
						return false;
					}
				} else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("add")) {
						String playerName = args[1];
						if(!playerName.equalsIgnoreCase(player.getName())) {
							UUID newUUID = plugin.getUUID(playerName);
							
							List<UUID> yellowListMembers = PlayerData.getYellowListMembers(player.getUniqueId());	
							if(newUUID != null) {
								playerName = plugin.getName(newUUID);
								
								boolean addPersonToList = true;
								for(UUID member : yellowListMembers) {
									if(member.equals(newUUID)) {
										addPersonToList = false;
									}
								}
								
								if(addPersonToList) {
									String notification = Messages.prefix_Good + Messages.message_yellowAdd;
									notification = notification.replace("{TARGET}", playerName);
									PlayerData.addPlayerToYellowList(player.getUniqueId(), newUUID);
									player.sendMessage(Utils.colorCodes(notification));
									return true;
								} else {
									String notification = Messages.prefix_Bad + Messages.error_yellowDuplicate;
									notification = notification.replace("{TARGET}", playerName);
									player.sendMessage(Utils.colorCodes(notification));
									return true;
								}
							} else {
								String notification = Messages.prefix_Bad + Messages.error_notValidPlayer;
								notification = notification.replace("{TARGET}", playerName);
								player.sendMessage(Utils.colorCodes(notification));
								return true;
							}
						} else {
							String notification = Messages.prefix_Bad + "You cannot add yourself to your own yellow list.";
							player.sendMessage(Utils.colorCodes(notification));
						}
						return true;
					} else if(args[0].equalsIgnoreCase("remove")) {
						String playerName = args[1];
						if(!playerName.equalsIgnoreCase(player.getName())) {
							UUID newUUID = plugin.getUUID(playerName);

							List<UUID> yellowListMembers = PlayerData.getYellowListMembers(player.getUniqueId());							
							if(newUUID != null) {
								playerName = plugin.getName(newUUID);
								boolean removePersonFromList = false;
								for(UUID member : yellowListMembers) {
									if(member.equals(newUUID)) {
										removePersonFromList = true;
									}
								}
								
								if(removePersonFromList) {
									String notification = Messages.prefix_Good + Messages.message_yellowRemove;
									notification = notification.replace("{TARGET}", playerName);
									PlayerData.removePlayerFromYellowList(player.getUniqueId(), newUUID);
									player.sendMessage(Utils.colorCodes(notification));
									
									//Check if the player is set to yellow and they are currently in a party with the person they just removed
									if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.YELLOW) {
										int partyId = PlayerData.getPartyID(player.getUniqueId());
										if(partyId > 0) {
											List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
											if(partyMembers.contains(newUUID)) {
												try {
													//Create a new party
													int newPartyId = PartyUtils.createParty(player.getName(), player.getUniqueId());
													
													//Update party id
													File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + player.getUniqueId().toString() + ".xml");
													DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
													DocumentBuilder docBuilder = docFactory.newDocumentBuilder();			
													Document doc = docBuilder.parse(xml);
													
													NodeList nList = doc.getElementsByTagName("party");
													Node tempNode = nList.item(0);
													tempNode.setTextContent(newPartyId + "");
													
													//Save
													TransformerFactory transformerFactory = TransformerFactory.newInstance();
													Transformer transformer = transformerFactory.newTransformer();
													DOMSource source = new DOMSource(doc);
													StreamResult result = new StreamResult(xml);
													transformer.setOutputProperty(OutputKeys.INDENT, "no");
													transformer.transform(source, result);
													
													//Remove player from old party
													PartyUtils.removePlayerParty(partyId, player.getUniqueId());
													
													//Notify old party member that they have left the party
													for(UUID uuid2 : partyMembers) {
														if(!uuid2.equals(player.getUniqueId())) {
															Player cPlayer = plugin.getServer().getPlayer(uuid2);
															if(cPlayer != null) {
																//Leave party message
																String msg = Messages.message_leaveParty;
																msg = msg.replace("{PLAYER}", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
																String reason = Messages.reasonNotOnYellowList;
																reason = reason.replace("{Player}", Color.colorCode(PlayerData.getPlayerColor(newUUID)) + playerName);
																msg = msg.replace(" {PLAYERS} {REASON}", ChatColor.RESET + reason);
																cPlayer.sendMessage(Utils.colorCodes(msg));
															}
														}
													}
													
													//Send Message to player who just left
													String partyMemberlist = "";
													for(UUID playerUUID : partyMembers) {
														if(!playerUUID.equals(player.getUniqueId()))
														partyMemberlist = partyMemberlist + ", " + Color.colorCode(PlayerData.getPlayerColor(playerUUID)) + plugin.getName(playerUUID);
													}
													partyMemberlist = partyMemberlist.substring(2);
													
													String msg = Messages.message_youLeaveParty;
													msg = msg.replace("has", "have");
													msg = msg.replace("{PLAYERS}", partyMemberlist);
													String reason = Messages.reasonNotOnYourYellowList;
													reason = reason.replace("{Player}", Color.colorCode(PlayerData.getPlayerColor(newUUID)) + playerName);
													msg = msg.replace("{REASON}", ChatColor.RESET + reason);
													player.sendMessage(Utils.colorCodes(msg));
													
												} catch(Exception e) {
													e.printStackTrace();
												}												
											}
										}
									}
									
									return true;
								} else {
									String notification = Messages.prefix_Bad + Messages.error_yellowNoMatch;
									notification = notification.replace("{TARGET}", playerName);
									player.sendMessage(Utils.colorCodes(notification));
									return true;
								}
							} else {
								String notification = Messages.prefix_Bad + Messages.error_notValidPlayer;
								notification = notification.replace("{TARGET}", playerName);
								player.sendMessage(Utils.colorCodes(notification));
								return true;
							}
						} else {
							String notification = Messages.prefix_Bad + "You cannot remove yourself from your own yellow list.";
							player.sendMessage(Utils.colorCodes(notification));
							return true;
						}
					} else {
						sender.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
						return false;
					}
				} else {
					sender.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
					return false;
				}
			}
		} else {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
	}
}
