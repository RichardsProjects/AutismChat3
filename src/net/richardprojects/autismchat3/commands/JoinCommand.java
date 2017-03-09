package net.richardprojects.autismchat3.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import net.richardprojects.autismchat3.ACPlayer;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PartyUtils;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {
private AutismChat3 plugin;
	
	public JoinCommand(AutismChat3 plugin) {
		this.plugin = plugin;		
	}
	
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			final String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			if(args.length == 1) {
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					public void run() {
						JoinCommand.this.run(player, args);
					}
				});
				return true;
			} else {
				String msg = Messages.prefix_Bad + Messages.error_invalidArgs;
				player.sendMessage(Utils.colorCodes(msg));
				return false;
			}
		} else {
			
			return true;
		}
	}
	
	private void run(Player player, String[] args) {
		UUID newUUID = plugin.getUUID(args[0]);
		ACPlayer acPlayer = plugin.getACPlayer(player.getUniqueId());
		ACPlayer acTarget = plugin.getACPlayer(newUUID);
		
		if(newUUID != null) {
			boolean joinParty = true;
			int partyId = acTarget.getPartyId();
			
			if(partyId == 0) {
				player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + "&7That player doesn't appear to be in a party."));
				return;
			}
			
			List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
			
			// check if the player is red - Check 1
			if(acTarget.getColor() == Color.RED) {
				joinParty = false;
				String msg = Messages.prefix_Bad + Messages.error_JoinParty1;
				String pName = Color.colorCode(Color.RED) + plugin.getName(newUUID);
				msg = msg.replace("{PLAYER}", pName);
				player.sendMessage(Utils.colorCodes(msg));
				return;
			}
			
			// check 6
			if(newUUID.equals(player.getUniqueId())) {
				joinParty = false;
				String msg = Messages.prefix_Bad + Messages.error_JoinParty6;
				player.sendMessage(Utils.colorCodes(msg));
				return;
			}
			
			// check 2
			String partyMemberString = Utils.partyMembersString(plugin, partyId, player.getUniqueId());
			boolean joinPartyAltered = false;
			joinParty = false;
			for (UUID uuid : partyMembers) {
				if (!uuid.equals(player.getUniqueId())) {
					ACPlayer acUUID = plugin.getACPlayer(uuid);
					if (acUUID.getColor() == Color.YELLOW) {
						List<UUID> uuids = acUUID.getYellowList();
						if (uuids.contains(player.getUniqueId())) {
							if (joinPartyAltered && !joinParty) {
								joinParty = false;
							} else if (joinPartyAltered && joinParty) {
								joinParty = true;
							} else if (!joinPartyAltered) {
								joinParty = true;
							}
							joinPartyAltered = true;
						}
					} else {
						if (joinPartyAltered && !joinParty) {
							joinParty = false;
						} else if (joinPartyAltered && joinParty) {
							joinParty = true;
						} else if (!joinPartyAltered) {
							joinParty = true;
						}
						joinPartyAltered = true;
					}
				} else {
					if (joinPartyAltered && !joinParty) {
						joinParty = false;
					} else if (joinPartyAltered && joinParty) {
						joinParty = true;
					} else if (!joinPartyAltered) {
						joinParty = true;
					}
					joinPartyAltered = true;
				}
			}
			if (!joinParty) {
				String msg = Messages.prefix_Bad + Messages.error_JoinParty2;
				msg = msg.replace("{MEMBERS}", partyMemberString);
				player.sendMessage(Utils.colorCodes(msg));
				return;
			}
			
			// check 3
			if(acPlayer.getColor() == Color.RED) {
				String msg = Messages.prefix_Bad + Messages.error_JoinParty3;
				player.sendMessage(Utils.colorCodes(msg));
				return;
			}
			
			// check 4
			if(acPlayer.getColor() == Color.YELLOW) {
				List<UUID> yellowListMembers = acPlayer.getYellowList();
				joinParty = false;
				
				// 0 - hasn't been determined
				// 1 - They can join
				// 2 - They can't join
				int canJoinParty = 0;
				for(UUID uuid : partyMembers) {
					if (!uuid.equals(player.getUniqueId())) {
						if(yellowListMembers.contains(uuid)) {
							if(canJoinParty == 0 || canJoinParty == 1) canJoinParty = 1;
						} else {
							if(canJoinParty == 0 || canJoinParty == 1) canJoinParty = 2;
						}
					}
				}
				
				// set joinParty variable from canJoinParty
				if(canJoinParty == 1) joinParty = true;
				if(canJoinParty == 2) joinParty = false;
				
				if(!joinParty) {
					String partyList = Utils.partyMembersString(plugin, partyId, player.getUniqueId());
					String msg = Messages.prefix_Bad + Messages.error_JoinParty4;
					msg = msg.replace("{MEMBERS}", partyList);
					player.sendMessage(Utils.colorCodes(msg));
					return;
				}
			}
			
			// check 5
			int currentPartyId = acPlayer.getPartyId();
			if(currentPartyId > 0) {
				List<UUID> currentPartyMembers = PartyUtils.partyMembers(currentPartyId);
				for(UUID uuid : currentPartyMembers) {
					if(uuid.equals(newUUID)) joinParty = false;
				}
				if(!joinParty) {
					String pName = Utils.formatName(plugin, newUUID, player.getUniqueId());
					String msg = Messages.prefix_Bad + Messages.error_JoinParty5;
					msg = msg.replace("{PLAYER}", pName);
					player.sendMessage(Utils.colorCodes(msg));
					return;
				}
			}
			
			
			// leave Party
			List<UUID> currentPartyMembers = PartyUtils.partyMembers(currentPartyId);
			File partyXml = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + currentPartyId + ".xml");
			if(partyXml.exists()) {
				if(currentPartyMembers.size() > 1) {
					// send messages to other members of the party that they left
					for(UUID playerId : currentPartyMembers) {
						if(!playerId.equals(player.getUniqueId())) {
							Player cPlayer = plugin.getServer().getPlayer(playerId);
							if(cPlayer != null) {
								String msg = Messages.message_leaveParty;
								String pName = Utils.formatName(plugin, player.getUniqueId(), cPlayer.getUniqueId());
								msg = msg.replace("{PLAYER}", pName);
								msg = msg.replace("{PLAYERS} {REASON}", Messages.reasonJoinedAnotherParty);
								cPlayer.sendMessage(Utils.colorCodes(msg));
							}
						} else {
							String msg = Messages.message_youLeaveParty;
							
							// create party list
							String partyMemberString2 = "";
							for(UUID member : currentPartyMembers) {
								String playerName = plugin.getName(member);
								if(playerName != null) {
									playerName = Utils.formatName(plugin, member, player.getUniqueId());
									partyMemberString2 += ", " + playerName;
								}
							}
							partyMemberString2 = partyMemberString2.substring(2);
							msg = msg.replace("{PLAYERS} {REASON}", partyMemberString2);
							player.sendMessage(Utils.colorCodes(msg));
						}
					}
				} else {
					// delete the file
					partyXml.delete();
				}
			}
			
			
			// join the party
			PartyUtils.joinParty(plugin, partyId, player.getUniqueId());
			
			// have player leave their current party
			PartyUtils.removePlayerParty(currentPartyId, player.getUniqueId());
			
			// send Join Messages
			int newPartyId = acPlayer.getPartyId();
			List<UUID> newPartyMemberlist = PartyUtils.partyMembers(newPartyId);
			for(UUID member : newPartyMemberlist) {
				if(!member.equals(player.getUniqueId())) {
					// send join message to member
					Player cPlayer = plugin.getServer().getPlayer(member);
					if(cPlayer != null) {
						String msg = Messages.message_joinParty;
						String name = Utils.formatName(plugin, player.getUniqueId(), cPlayer.getUniqueId());
						msg = msg.replace("{PLAYER}", name);
						msg = msg.replace(" {MEMBERS}", "");
						cPlayer.sendMessage(Utils.colorCodes(msg));
					}
				} else {
					// send join message to player
					Player cPlayer = plugin.getServer().getPlayer(member);
					if(cPlayer != null) {
						// create members list
						String partyMemberlist = "";
						for(UUID playerUUID : newPartyMemberlist) {
							String name = plugin.getName(playerUUID);
							if(!name.equalsIgnoreCase(player.getName())) {
								partyMemberlist += ", " + Utils.formatName(plugin, playerUUID, player.getUniqueId());
							}
						}
						partyMemberlist = partyMemberlist.substring(2);
						
						String msg = Messages.message_youJoinParty;
						msg = msg.replace("has", "have");
						msg = msg.replace("{MEMBERS}", partyMemberlist);
						cPlayer.sendMessage(Utils.colorCodes(msg));
					}
				}
			}
			
		} else {
			String msg = Messages.prefix_Bad + Messages.error_notValidPlayer;
			msg = msg.replace("{TARGET}", args[0]);
			player.sendMessage(Utils.colorCodes(msg));
		}
	}
}
