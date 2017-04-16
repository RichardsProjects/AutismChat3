package net.richardprojects.autismchat3.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import net.richardprojects.autismchat3.ACParty;
import net.richardprojects.autismchat3.ACPlayer;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
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
		int partyId = acTarget.getPartyId();
		boolean joinParty = true;
		
		// make sure the player's name exists
		if (newUUID == null) {
			String msg = Messages.prefix_Bad + Messages.error_notValidPlayer;
			msg = msg.replace("{TARGET}", args[0]);
			player.sendMessage(Utils.colorCodes(msg));
		}
		
		// make sure they are in a party
		if(partyId == 0 || plugin.getACParty(partyId) == null) {
			String msg = Messages.prefix_Bad + "&7That player doesn't appear to be in a party.";
			player.sendMessage(Utils.colorCodes(msg));
			return;
		}
				
		List<UUID> partyMembers = plugin.getACParty(partyId).getMembers();
		
		// check if the player is red - (Check 1)
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
		if (plugin.getACParty(currentPartyId) != null) {
			List<UUID> cPartyMembers = plugin.getACParty(currentPartyId).getMembers();
			for(UUID uuid : cPartyMembers) {
				if(uuid.equals(newUUID)) joinParty = false;
			}
			if (!joinParty) {
				String pName = Utils.formatName(plugin, newUUID, player.getUniqueId());
				String msg = Messages.prefix_Bad + Messages.error_JoinParty5;
				msg = msg.replace("{PLAYER}", pName);
				player.sendMessage(Utils.colorCodes(msg));
				return;
			}
		}
		
		plugin.joinParty(partyId, player.getUniqueId()); // join new party
	}
}
