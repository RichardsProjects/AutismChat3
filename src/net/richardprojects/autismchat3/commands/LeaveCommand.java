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
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PartyUtils;
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
	
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			final String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				public void run() {
					if(args.length == 0) {
						int partyId = plugin.getACPlayer(player.getUniqueId()).getPartyId();
						if(partyId > 0) {
							List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
							if(partyMembers.size() > 1) {
								try {
									// create a new party and update id
									int newPartyId = PartyUtils.createParty(player.getName(), player.getUniqueId());
									plugin.getACPlayer(player.getUniqueId()).setPartyId(newPartyId);
									
									// remove player from old party
									PartyUtils.removePlayerParty(partyId, player.getUniqueId());
									
									// notify old party member that they have left the party
									partyMembers = PartyUtils.partyMembers(partyId);
									for(UUID uuid2 : partyMembers) {
										if(!uuid2.equals(player.getUniqueId())) {
											Player cPlayer = plugin.getServer().getPlayer(uuid2);
											if(cPlayer != null) {
												//Leave party message
												String msg = Messages.message_leaveParty;
												String pName = Utils.formatName(plugin, player.getUniqueId(), cPlayer.getUniqueId());
												msg = msg.replace("{PLAYER}", pName);
												msg = msg.replace(" {PLAYERS} {REASON}", "");
												cPlayer.sendMessage(Utils.colorCodes(msg));
											}
										}
									}
									
									// send Message to player who just left
									String partyMemberlist = "";
									for (UUID playerUUID : partyMembers) {
										if (!playerUUID.equals(player.getUniqueId())) {
											String pName = Utils.formatName(plugin, playerUUID, player.getUniqueId());
											partyMemberlist += ", " + pName;
										}
									}
									partyMemberlist = partyMemberlist.substring(2);
									
									String msg = Messages.message_youLeaveParty;
									msg = msg.replace("has", "have");
									msg = msg.replace("{PLAYERS}", partyMemberlist);
									msg = msg.replace(" {REASON}", "");
									player.sendMessage(Utils.colorCodes(msg));
									
								} catch(Exception e) {
									e.printStackTrace();
								}
							} else {
								player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.message_onlyOneInParty));
							}
						}
					} else {
						String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
						player.sendMessage(msg);
						player.sendMessage("/leave");
					}
				}
			});
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}
		
		return true;
	}

}
