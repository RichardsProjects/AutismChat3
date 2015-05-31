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

public class RedCommand implements CommandExecutor {

	private AutismChat3 plugin;
	
	public RedCommand(AutismChat3 plugin) {
		this.plugin = plugin;		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			if(args.length == 0)
			{
				PlayerData.setColor(player.getUniqueId(), Color.RED);
				String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_setRed);
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
				AutismChat3.redTeam.addPlayer(player);
				player.sendMessage(msg);
				for(Player cPlayer : plugin.getServer().getOnlinePlayers()) {
					cPlayer.setScoreboard(AutismChat3.board);
				}
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						//Remove player from their party if there is more than just them in it
						int currentPartyId = PlayerData.getPartyID(player.getUniqueId());
						List<UUID> currentPartyMemberList = PartyUtils.partyMembers(currentPartyId);
						if(currentPartyMemberList.size() > 1) {
							try {
								//Message everyone
								for(UUID member : currentPartyMemberList) {
									if(!member.equals(player.getUniqueId())) {
										Player cPlayer = plugin.getServer().getPlayer(member);
										if(cPlayer != null) {
											String msg2 = Messages.message_leaveParty;
											msg2 = msg2.replace("{PLAYER}", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
											msg2 = msg2.replace("{PLAYERS} {REASON}", Messages.reasonLeaveRed);
											cPlayer.sendMessage(Utils.colorCodes(msg2));
										}
									} else {
										String partyMemberlist = "";
										for(UUID playerUUID : currentPartyMemberList) {
											partyMemberlist = partyMemberlist + ", " + plugin.getName(playerUUID);
										}
										partyMemberlist = partyMemberlist.substring(2);
										
										String msg2 = Messages.message_youLeaveParty;
										msg2 = msg2.replace("{PLAYERS}", partyMemberlist);
										msg2 = msg2.replace("{REASON}", Messages.reasonYouRed);
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
								transformer.setOutputProperty(OutputKeys.INDENT, "yes");
								transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
								transformer.transform(source, result);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			} else {
				String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
				player.sendMessage(msg);
				return false;
			}
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}		
		return true;
	}
}