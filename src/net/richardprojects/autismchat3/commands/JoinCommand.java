package net.richardprojects.autismchat3.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

public class JoinCommand implements CommandExecutor {
private AutismChat3 plugin;
	
	public JoinCommand(AutismChat3 plugin) {
		this.plugin = plugin;		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			final String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			if(args.length == 1) {
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						UUID newUUID = plugin.getUUID(args[0]);
						if(newUUID != null) {
							boolean joinParty = true;
							int partyId = PlayerData.getPartyID(newUUID);
							
							if(partyId == 0) {
								player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + "&7That player doesn't appear to be in a party."));
								return;
							}
							
							List<UUID> partyMembers = PartyUtils.partyMembers(partyId);
							
							//Check if the player is red - Check 1
							if(PlayerData.getPlayerColor(newUUID) == Color.RED) {
								joinParty = false;
								String msg = Messages.prefix_Bad + Messages.error_JoinParty1;
								player.sendMessage(Utils.colorCodes(msg));
								return;
							}
							
							//Check 6
							if(newUUID.equals(player.getUniqueId())) {
								joinParty = false;
								String msg = Messages.prefix_Bad + Messages.error_JoinParty6;
								player.sendMessage(Utils.colorCodes(msg));
								return;
							}
							
							//Check 2
							String partyMemberString = "";
							//Create party list
							for(UUID member : partyMembers) {
								String playerName = plugin.getName(member);
								if(playerName != null) {
									playerName = Color.colorCode(PlayerData.getPlayerColor(member)) + playerName;
									partyMemberString = partyMemberString + ", " + playerName;
								}
							}
							partyMemberString = partyMemberString.substring(2);
							for(UUID uuid : partyMembers) {
								if(PlayerData.getPlayerColor(uuid) == Color.YELLOW) {
									joinParty = false;
									List<UUID> uuids = PlayerData.getYellowListMembers(uuid);
									if(uuids.contains(player.getUniqueId())) {
										joinParty = true;
									}
								}
								if(!joinParty) {
									String msg = Messages.prefix_Bad + Messages.error_JoinParty2;
									msg = msg.replace("{MEMBERS}", partyMemberString);
									player.sendMessage(Utils.colorCodes(msg));
									return;
								}
							}
							
							//Check 3
							if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.RED) {
								String msg = Messages.prefix_Bad + Messages.error_JoinParty3;
								player.sendMessage(Utils.colorCodes(msg));
								return;
							}
							
							//Check 4
							if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.YELLOW) {
								List<UUID> yellowListMembers = PlayerData.getYellowListMembers(player.getUniqueId());
								joinParty = false;
								for(UUID uuid : partyMembers) {
									if(yellowListMembers.contains(uuid)) {
										joinParty = true;
									} else {
										joinParty = false;
									}
									if(!joinParty) {
										String msg = Messages.prefix_Bad + Messages.error_JoinParty4;
										player.sendMessage(Utils.colorCodes(msg));
										return;
									}
								}
							}
							
							//Check 5
							int currentPartyId = PlayerData.getPartyID(player.getUniqueId());
							if(currentPartyId > 0) {
								List<UUID> currentPartyMembers = PartyUtils.partyMembers(currentPartyId);
								for(UUID uuid : currentPartyMembers) {
									if(uuid.equals(newUUID)) joinParty = false;
								}
								if(!joinParty) {
									String msg = Messages.prefix_Bad + Messages.error_JoinParty5;
									player.sendMessage(Utils.colorCodes(msg));
									return;
								}
							}
							
							
							//Leave Party
							List<UUID> currentPartyMembers = PartyUtils.partyMembers(currentPartyId);
							File partyXml = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + currentPartyId + ".xml");
							if(partyXml.exists()) {
								if(currentPartyMembers.size() > 1) {
									//Send messages to other members of the party that they left
									for(UUID playerId : currentPartyMembers) {
										if(!playerId.equals(player.getUniqueId())) {
											Player cPlayer = plugin.getServer().getPlayer(playerId);
											if(cPlayer != null) {
												String msg = Messages.message_leaveParty;
												msg = msg.replace("{PLAYER}", player.getName());
												msg = msg.replace(" {PLAYERS} {REASON}", " because they have joined another party.");
												cPlayer.sendMessage(Utils.colorCodes(msg));
											}
										}
									}
								} else {
									//Delete the file
									partyXml.delete();
								}
							}
							
							
							//Join the party
							PartyUtils.joinParty(partyId, player.getUniqueId());
							
							//Have player leave their current party
							PartyUtils.removePlayerParty(currentPartyId, player.getUniqueId());
							
							//Send Join Messages
							int newPartyId = PlayerData.getPartyID(player.getUniqueId());
							List<UUID> newPartyMemberlist = PartyUtils.partyMembers(newPartyId);
							for(UUID member : newPartyMemberlist) {
								if(!member.equals(player.getUniqueId())) {
									//Send join message to member
									Player cPlayer = plugin.getServer().getPlayer(member);
									if(cPlayer != null) {
										String msg = Messages.message_joinParty;
										msg = msg.replace("{PLAYER}", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
										msg = msg.replace(" {MEMBERS}", "");
										cPlayer.sendMessage(Utils.colorCodes(msg));
									}
								} else {
									//Send join message to player
									Player cPlayer = plugin.getServer().getPlayer(member);
									if(cPlayer != null) {
										//Create members list
										String partyMemberlist = "";
										for(UUID playerUUID : newPartyMemberlist) {
											String name = plugin.getName(playerUUID);
											if(!name.equalsIgnoreCase(player.getName())) {
												partyMemberlist = partyMemberlist + ", " + Color.colorCode(PlayerData.getPlayerColor(playerUUID)) + name;
											}
										}
										partyMemberlist = partyMemberlist.substring(2);
										
										String msg = Messages.message_joinParty;
										msg = msg.replace("{PLAYER}", "You");
										msg = msg.replace("has", "have");
										msg = msg.replace("{MEMBERS}", partyMemberlist);
										cPlayer.sendMessage(Utils.colorCodes(msg));
									}
								}
							}
							
						} else {
							String msg = Messages.prefix_Bad + Messages.error_notValidPlayer;
							player.sendMessage(Utils.colorCodes(msg));
						}
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
}
