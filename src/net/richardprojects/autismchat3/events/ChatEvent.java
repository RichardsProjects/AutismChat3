/*   This file is part of AutismChat3.
*
*    AutismChat3 is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License.
*
*    You can view a copy of the GNU General Public License below
*    http://www.gnu.org/licenses/
*/

package net.richardprojects.autismchat3.events;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PartyUtils;
import net.richardprojects.autismchat3.PlayerData;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {
	
	private AutismChat3 plugin;
	
	public ChatEvent(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatEvent(AsyncPlayerChatEvent e) {
		String chatMsg = e.getMessage();
		e.setCancelled(true);
		Player player2 = (Player) e.getPlayer();
		UUID uuid1 = player2.getUniqueId();
		String playerName = Color.colorCode(PlayerData.getPlayerColor(uuid1)) + player2.getName();
		
		int partyID = PlayerData.getPartyID(player2.getUniqueId());
		
		if(partyID > 0) {
			int playersSentTo = 0;
			for(UUID uuid : PartyUtils.partyMembers(partyID)) {
				Player cPlayer = plugin.getServer().getPlayer(uuid);
				if(cPlayer != null) {
					String msg = Messages.partyChatFormat;
					msg = msg.replace("%name%", playerName + ChatColor.RESET);
					msg = msg.replace("%message%", chatMsg);
					msg = Utils.colorCodes(msg);
					cPlayer.sendMessage(msg);
					playersSentTo++;
				}
			}
			
			if(playersSentTo == 1 || playersSentTo == 0) {
				String msg = Messages.prefix_Bad + Messages.message_nobodyHeardMessage;
				player2.sendMessage(Utils.colorCodes(msg));
			}
		}
	}
}