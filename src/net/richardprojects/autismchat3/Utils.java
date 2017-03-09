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
			if(partyId > 0) {
				List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
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
			if(yellowListMembers.size() > 0) {
				yellowListString = yellowListString.substring(2);
				msg = msg.replace("{yellow_list}", yellowListString);
			} else {
				msg = msg.replace("{yellow_list}", "NONE");
			}
			receiver.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("colourSetting")) {
			Color playersColor = uuidPlayer.getColor();
			String msg = Messages.status_colorSetting;
			
			msg = msg.replace("{COLORSTATUS}", Color.colorCode(playersColor) + Color.toString(playersColor));
			
			receiver.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("globalChat")) {
			String msg = Messages.status_globalChat;
			
			if(uuidPlayer.isGlobalChatEnabled()) {
				msg = msg.replace("{yesno}", "Yes");
			} else {
				msg = msg.replace("{yesno}", "No");
			}
			
			receiver.sendMessage(Utils.colorCodes(msg));
		} else if(status.equals("partyMembers")) {
			String msg = Messages.status_partyMembers;
			String name = Utils.formatName(plugin, uuid, receiver.getUniqueId());
			msg = msg.replace("{TARGET}", name);
			msg = msg.replace("{BEINGVERB}", "is");
			String partyMemberString = "";
			String onlineMemberString = "";
			
			int partyId = uuidPlayer.getPartyId();
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
							playerName = Utils.formatName(plugin, member, receiver.getUniqueId());
							partyMemberString += ", " + playerName;
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
					if(!member.equals(uuid)) {
						String playerName = plugin.getName(member);
						if(playerName != null) {
							playerName = Utils.formatName(plugin, member, receiver.getUniqueId());
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
				
				//Replace
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
	 * name
	 * @return formatted name
	 */
	public static String formatName(AutismChat3 plugin, UUID player, UUID perspective) {
		if(perspective != null) {
			if (player.equals(perspective)) {
				// format player name from their perspective, so "You"
				String name = "You";
				name = Color.colorCode(plugin.getACPlayer(player).getColor()) + name;
				return colorCodes(name + "&r");
			}
		}
		
		String name = plugin.getName(player);
		name = Color.colorCode(plugin.getACPlayer(player).getColor()) + name;
		return colorCodes(name + "&r");
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
		String partyMemberString = "";
		
		List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
		for(UUID member : partyMembers) {
			if(!member.equals(uuid)) {
				String playerName = plugin.getName(member);
				if(playerName != null) {
					playerName = Utils.formatName(plugin, member, uuid);
					partyMemberString += ", " + playerName;
				}
			}
		}
		if(partyMembers.size() > 1) {
			partyMemberString = partyMemberString.substring(2);
			partyMemberString = partyMemberString + "&r";
		} else {
			partyMemberString = "NONE";
		}
		
		return partyMemberString;
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