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
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static void sendStatus(String status, UUID uuid, AutismChat3 plugin) {
		Player player = plugin.getServer().getPlayer(uuid);
		ACPlayer acPlayer = plugin.getACPlayer(uuid);
		if(player == null || acPlayer == null) {
			return;
		}
		
		if(status.equals("yellowList")) {
			String msg = Messages.status_yellowList;
			String yellowListString = "";
			
			List<UUID> yellowListMembers = acPlayer.getYellowList();
			for(UUID member : yellowListMembers) {
				String playerName = plugin.getName(member);
				if(playerName != null) {
					playerName = Utils.formatName(plugin, member, player.getUniqueId());
					yellowListString += ", " + playerName;
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
			Color playersColor = acPlayer.getColor();
			String msg = Messages.status_colorSetting;
			
			msg = msg.replace("{COLORSTATUS}", Color.colorCode(playersColor) + Color.toString(playersColor));
			
			player.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("globalChat")) {
			String msg = Messages.status_globalChat;
			
			if(acPlayer.isGlobalChatEnabled()) {
				msg = msg.replace("{yesno}", "Yes");
			} else {
				msg = msg.replace("{yesno}", "No");
			}
			
			player.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("partyMembers")) {
			String msg = Messages.status_partyMembers;
			String pName = Utils.formatName(plugin, player.getUniqueId(), player.getUniqueId());
			msg = msg.replace("{TARGET}", pName);
			msg = msg.replace("{BEINGVERB}", "are");
			String onlineMemberString = "";
			
			int partyId = acPlayer.getPartyId();
			if(plugin.getACParty(partyId) != null) {
				List<UUID> partyMembers = plugin.getACParty(partyId).getMembers();
				List<UUID> onlineMembers = new ArrayList<UUID>();
				for(UUID member : partyMembers) {
					Player cPlayer = plugin.getServer().getPlayer(member);
					if(cPlayer != null) {
						onlineMembers.add(member);
					}
				}
				
				// create list of players in the party who are online
				for(UUID member : onlineMembers) {
					if(!member.equals(player.getUniqueId())) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Utils.formatName(plugin, member, player.getUniqueId());
							onlineMemberString += ", " + playerName;
						}
					}
				}
				if(onlineMembers.size() > 1) {
					onlineMemberString = onlineMemberString.substring(2);
					onlineMemberString = onlineMemberString + "&r";
				} else {
					onlineMemberString = "NONE";
				}
				
				// replace
				String partyMemberString = Utils.partyMembersString(plugin, partyId, player.getUniqueId());
				msg = msg.replace("{PARTYMEMBERS}", partyMemberString);
				msg = msg.replace("{ONLINEPARTYMEMBERS}", onlineMemberString);
			} else {
				msg = msg.replace("{PARTYMEMBERS}", "NONE");
				msg = msg.replace("{ONLINEPARTYMEMBERS}", "NONE");
			}
			
			// send message to player
			player.sendMessage(Utils.colorCodes(msg));
		} else if (status.equals("othersYellowList")) {
			String msg = Messages.status_othersYellowList;			
			String othersYellowList = "";
			
			// loop through all players and check their yellow lists.
			for (UUID playerUUID : plugin.playersUUIDs()) {
				ACPlayer currentACPlayer = plugin.getACPlayer(playerUUID);
				if (currentACPlayer == null) continue;
				
				if (currentACPlayer.getYellowList().contains(player.getUniqueId())) {
					String name = Utils.formatName(plugin, playerUUID, player.getUniqueId());
					othersYellowList += "&r, " + name;
				}
			}
			
			// clean up the list
			if (othersYellowList.length() > 3) {
				othersYellowList = othersYellowList.substring(4);
			} else {
				othersYellowList = "&rNONE";
			}
			
			msg = msg.replace("{others_yellow_list}", othersYellowList);			
			player.sendMessage(colorCodes(msg));
		}
	}
	
	public static void sendStatus(String status, Player receiver, UUID uuid, AutismChat3 plugin) {
		ACPlayer uuidPlayer = plugin.getACPlayer(uuid);
		
		if(status.equals("yellowList")) {
			String msg = Messages.status_yellowList;
			String yellowListString = "";
			
			List<UUID> yellowListMembers = uuidPlayer.getYellowList();
			for(UUID member : yellowListMembers) {
				String playerName = plugin.getName(member);
				if(playerName != null) {
					playerName = Utils.formatName(plugin, member, receiver.getUniqueId());
					yellowListString += ", " + playerName;
				}
			}
			if (yellowListMembers.size() > 0) {
				yellowListString = yellowListString.substring(2);
				msg = msg.replace("{yellow_list}", yellowListString);
			} else {
				msg = msg.replace("{yellow_list}", "NONE");
			}
			receiver.sendMessage(Utils.colorCodes(msg));
		} else if (status.equals("colourSetting")) {
			Color playersColor = uuidPlayer.getColor();
			String msg = Messages.status_colorSetting;
			
			msg = msg.replace("{COLORSTATUS}", Color.colorCode(playersColor) + Color.toString(playersColor));
			
			receiver.sendMessage(Utils.colorCodes(msg));
		} else if (status.equals("globalChat")) {
			String msg = Messages.status_globalChat;
			
			if(uuidPlayer.isGlobalChatEnabled()) {
				msg = msg.replace("{yesno}", "Yes");
			} else {
				msg = msg.replace("{yesno}", "No");
			}
			
			receiver.sendMessage(Utils.colorCodes(msg));
		} else if (status.equals("partyMembers")) {
			String msg = Messages.status_partyMembers;
			String name = Utils.formatName(plugin, uuid, receiver.getUniqueId());
			msg = msg.replace("{TARGET}", name);
			msg = msg.replace("{BEINGVERB}", "is");
			String partyMemberString = "";
			String onlineMemberString = "";
			
			int partyId = uuidPlayer.getPartyId();
			if (plugin.getACParty(partyId) != null) {
				List<UUID> partyMembers = plugin.getACParty(partyId).getMembers();
				List<UUID> onlineMembers = new ArrayList<UUID>();
				for(UUID member : partyMembers) {
					Player cPlayer = plugin.getServer().getPlayer(member);
					if(cPlayer != null) {
						onlineMembers.add(member);
					}
				}
				
				// create party list
				for (UUID member : partyMembers) {
					if(!member.equals(uuid)) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Utils.formatName(plugin, member, receiver.getUniqueId());
							partyMemberString += ", " + playerName;
						}
					}
				}
				if (partyMembers.size() > 1) {
					partyMemberString = partyMemberString.substring(2);
					partyMemberString = partyMemberString + "&r";
				} else {
					partyMemberString = "NONE";
				}
				
				// create list of players in the party who are online
				for (UUID member : onlineMembers) {
					if(!member.equals(uuid)) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Utils.formatName(plugin, member, receiver.getUniqueId());
							onlineMemberString += ", " + playerName;
						}
					}
				}
				if (onlineMembers.size() > 1) {
					onlineMemberString = onlineMemberString.substring(2);
					onlineMemberString = onlineMemberString + "&r";
				} else {
					onlineMemberString = "NONE";
				}
				
				// replace variables
				msg = msg.replace("{PARTYMEMBERS}", partyMemberString);
				msg = msg.replace("{ONLINEPARTYMEMBERS}", onlineMemberString);
			} else {
				msg = msg.replace("{PARTYMEMBERS}", "NONE");
				msg = msg.replace("{ONLINEPARTYMEMBERS}", "NONE");
			}
			
			//Send Message to player
			receiver.sendMessage(Utils.colorCodes(msg));
		}
	}
	
	/**
	 * This method generates a formatted name with the proper color of the
	 * player who's UUID is provided.
	 * 
	 * @param plugin an instance of the AutismChat3 plugin
	 * @param player the UUID of the player's name to be formatted
	 * @param perspective the perspective of the player to see the formatted 
	 * name (can be null)
	 * @return formatted name
	 */
	public static String formatName(AutismChat3 plugin, UUID player, UUID perspective) {
		if (perspective != null && player.equals(perspective)) {
			// format player name from their perspective, so "You"
			String name = "You";
			name = Color.colorCode(plugin.getACPlayer(player).getColor()) + name;
			return colorCodes(name + "&r");
		} else {
			String name = plugin.getName(player);
			name = Color.colorCode(plugin.getACPlayer(player).getColor()) + name;
			return colorCodes(name + "&r");
		}
	}
	
	/**
	 * This method creates a formatted list of members in the specified party.
	 * 
	 * @param plugin A reference to the AutismChat3 plugin
	 * @param partyId the id of the party to generate a member list for
	 * @param uuid the player's perspective that this is from
	 * @return
	 */
	public static String partyMembersString(AutismChat3 plugin, int partyId, UUID uuid) {
		if (plugin.getACParty(partyId) == null) return null;
		
		String partyMemberString = "";
		List<UUID> partyMembers = plugin.getACParty(partyId).getMembers();
		for(UUID member : partyMembers) {
			if(!member.equals(uuid)) {
				String playerName = plugin.getName(member);
				if(playerName != null) {
					playerName = Utils.formatName(plugin, member, uuid);
					partyMemberString += ", " + playerName;
				}
			}
		}
		if (!partyMemberString.equals("")) {
			partyMemberString = partyMemberString.substring(2);
			partyMemberString = partyMemberString + "&r";
		} else {
			partyMemberString = "NONE";
		}
		
		return partyMemberString;
	}
	
	/**
	 * Takes a list of UUIDs, intended to be the player's yellow list, and 
	 * returns a String with formatted names
	 * 
	 * @param plugin an instance of the AutismChat3 plugin
	 * @param list the list of UUIDs
	 * @return the formatted String
	 */
	public static String UUIDListToFormattedString(AutismChat3 plugin, List<UUID> list) {
		String result = "";
		
		for (UUID member : list) {
			String playerName = plugin.getName(member);
			
			if (playerName != null) {
				playerName = Utils.formatName(plugin, member, null);
				result += ", " + playerName;
			}
		}
		
		if (list.size() > 1) {
			result = result.substring(2);
			result += "&r";
		} else {
			result = "NONE";
		}
		
		return result;
	}
	
	/**
	 * This method takes a List and returns a String representation of it.
	 * 
	 * @param list the list to convert to a String
	 * @return the String that representation of the List
	 */
	public static String convertListToString(List<UUID> list) {
		String result = "";
		
		List<UUID> newList = new ArrayList<>(list);
		for (UUID uuid : newList) {
			result = result + uuid.toString() + ",";
		}
		if (result.length() > 0) result = result.substring(0, result.length() - 1);
		
		return result;
	}
	
	/**
	 * This method takes a String a converts it into a List.
	 * 
	 * @param str the String to convert to a list
	 * @return the list representation of the String
	 */
	public static List<UUID> convertStringToList(String str) {
		ArrayList<UUID> list = new ArrayList<UUID>();
		
		for (String tmp : str.split(",")) {
			try {
				UUID current = UUID.fromString(tmp);
				list.add(current);
			} catch (Exception e) {
				
			}
		}
		
		return list;
	}
}