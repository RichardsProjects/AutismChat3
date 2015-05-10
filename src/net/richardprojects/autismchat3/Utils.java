/*   This file is part of AutismChat3.
*
*    AutismChat3 is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License.
*
*    You can view a copy of the GNU General Public License below
*    http://www.gnu.org/licenses/
*/

package net.richardprojects.autismchat3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Utils {

	public static String colorCodes(String msg) {
		msg = msg.replace("&0", ChatColor.BLACK + "");
		msg = msg.replace("&1", ChatColor.DARK_BLUE + "");
		msg = msg.replace("&2", ChatColor.DARK_GREEN + "");
		msg = msg.replace("&3", ChatColor.DARK_AQUA + "");
		msg = msg.replace("&4", ChatColor.DARK_RED + "");
		msg = msg.replace("&5", ChatColor.DARK_PURPLE + "");
		msg = msg.replace("&6", ChatColor.GOLD + "");
		msg = msg.replace("&7", ChatColor.GRAY + "");
		msg = msg.replace("&8", ChatColor.DARK_GRAY + "");
		msg = msg.replace("&9", ChatColor.BLUE + "");
		msg = msg.replace("&a", ChatColor.GREEN + "");
		msg = msg.replace("&b", ChatColor.AQUA + "");
		msg = msg.replace("&c", ChatColor.RED + "");
		msg = msg.replace("&d", ChatColor.LIGHT_PURPLE + "");
		msg = msg.replace("&e", ChatColor.YELLOW + "");
		msg = msg.replace("&f", ChatColor.WHITE + "");
		msg = msg.replace("&l", ChatColor.BOLD + "");
		msg = msg.replace("&m", ChatColor.STRIKETHROUGH + "");
		msg = msg.replace("&n", ChatColor.UNDERLINE + "");
		msg = msg.replace("&o", ChatColor.ITALIC + "");
		msg = msg.replace("&r", ChatColor.RESET + "");
		return msg;
	}
	
	public static void sendStatus(String status, UUID uuid, AutismChat3 plugin) {
		Player player = plugin.getServer().getPlayer(uuid);
		if(player == null) {
			return;
		}
		
		if(status.equals("yellowList")) {
			String msg = Messages.status_yellowList;
			String yellowListString = "";
			
			List<UUID> yellowListMembers = PlayerData.getYellowListMembers(uuid);
			for(UUID member : yellowListMembers) {
				String playerName = plugin.getName(member);
				if(playerName != null) {
					playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
					yellowListString = yellowListString + ", " + playerName;
				}
			}
			if(yellowListMembers.size() > 0) {
				yellowListString = yellowListString.substring(2);
				msg = msg.replace("{yellow_list}", yellowListString);
			} else {
				msg = msg.replace("{yellow_list}", "NONE");
			}
			player.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("colourSetting")) {
			Color playersColor = PlayerData.getPlayerColor(uuid);
			String msg = Messages.status_colorSetting;
			
			msg = msg.replace("{COLORSTATUS}", Color.colorCode(playersColor) + Color.toString(playersColor));
			
			player.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("globalChat")) {
			String msg = Messages.status_globalChat;
			
			if(PlayerData.globalChatEnabled(uuid)) {
				msg = msg.replace("{yesno}", "Yes");
			} else {
				msg = msg.replace("{yesno}", "No");
			}
			
			player.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("partyMembers")) {
			String msg = Messages.status_partyMembers;
			String partyMemberString = "";
			String onlineMemberString = "";
			
			int partyId = PlayerData.getPartyID(uuid);
			if(partyId > 0) {
				List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
				List<UUID> onlineMembers = new ArrayList<UUID>();
				for(UUID member : partyMembers) {
					Player cPlayer = plugin.getServer().getPlayer(member);
					if(cPlayer != null) {
						onlineMembers.add(member);
					}
				}
				
				//Create party list
				for(UUID member : partyMembers) {
					if(!member.equals(uuid)) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
							partyMemberString = partyMemberString + ", " + playerName;
						}
					}
				}
				if(partyMembers.size() > 1) {
					partyMemberString = partyMemberString.substring(2);
					partyMemberString = partyMemberString + "&r";
				} else {
					partyMemberString = "NONE";
				}
				
				//Create list of players in the party who are online
				for(UUID member : onlineMembers) {
					if(!member.equals(player.getUniqueId())) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
							onlineMemberString = onlineMemberString + ", " + playerName;
						}
					}
				}
				if(onlineMembers.size() > 1) {
					onlineMemberString = onlineMemberString.substring(2);
					onlineMemberString = onlineMemberString + "&r";
				} else {
					onlineMemberString = "NONE";
				}
				
				//Replace
				msg = msg.replace("{PARTYMEMBERS}", partyMemberString);
				msg = msg.replace("{ONLINEPARTYMEMBERS}", onlineMemberString);
			} else {
				msg = msg.replace("{PARTYMEMBERS}", "NONE");
				msg = msg.replace("{ONLINEPARTYMEMBERS}", "NONE");
			}
			
			//Send Message to player
			player.sendMessage(Utils.colorCodes(msg));
		}
	}
}