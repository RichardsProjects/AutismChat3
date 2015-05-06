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
import net.richardprojects.autismchat3.Config;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PartyUtils;
import net.richardprojects.autismchat3.PlayerData;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LeaveCommand implements CommandExecutor {

	private AutismChat3 plugin;
	
	public LeaveCommand(AutismChat3 plugin) {
		this.plugin = plugin;		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			final String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				@Override
				public void run() {
					if(args.length == 0) {
						int partyId = PlayerData.getPartyID(player.getUniqueId());
						if(partyId > 0) {
							List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
							if(partyMembers.size() > 1) {
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
									partyMembers = PartyUtils.partyMembers(partyId);
									for(UUID uuid2 : partyMembers) {
										if(!uuid2.equals(player.getUniqueId())) {
											Player cPlayer = plugin.getServer().getPlayer(uuid2);
											if(cPlayer != null) {
												//Leave party message
												String msg = Messages.message_leaveParty;
												msg = msg.replace("{PLAYER}", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
												msg = msg.replace(" {PLAYERS} {REASON}", "");
												cPlayer.sendMessage(Utils.colorCodes(msg));
											}
										}
									}
									
									//Send Message to player who just left
									String partyMemberlist = "";
									for(UUID playerUUID : partyMembers) {
										partyMemberlist = partyMemberlist + ", " + Color.colorCode(PlayerData.getPlayerColor(playerUUID)) + plugin.getName(playerUUID);
									}
									partyMemberlist = partyMemberlist.substring(2);
									
									String msg = Messages.message_leaveParty;
									msg = msg.replace("{PLAYER}", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + "You");
									msg = msg.replace("has", "have");
									msg = msg.replace("{PLAYERS}", partyMemberlist);
									msg = msg.replace(" {REASON}", "");
									player.sendMessage(Utils.colorCodes(msg));
									
								} catch(Exception e) {
									e.printStackTrace();
								}
							} else {
								/*try {
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
									
									//Delete the old party
									File oldParty = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + partyId + ".xml");
									oldParty.delete();
								} catch (Exception e) {
									e.printStackTrace();
									player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + " There was an error while attempting to leave the party."));
								}*/
								player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.message_onlyOneInParty));
							}
						}
					}
				}
			});
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}
		
		return true;
	}

}
