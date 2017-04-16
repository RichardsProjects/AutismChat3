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

import net.richardprojects.autismchat3.ACParty;
import net.richardprojects.autismchat3.AutismChat3;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PDeathEvent implements Listener {
	
	private AutismChat3 plugin;
	
	public PDeathEvent(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDeathEvent(PlayerDeathEvent e) {
		final String chatMsg = e.getDeathMessage();
		e.setDeathMessage("");
		final Player player = e.getEntity();
		
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			public void run() {
				int partyID = plugin.getACPlayer(player.getUniqueId()).getPartyId();
				ACParty party = plugin.getACParty(partyID);
				
				if(party != null) {
					for(UUID uuid : party.getMembers()) {
						Player cPlayer = plugin.getServer().getPlayer(uuid);
						if(cPlayer != null) {
							cPlayer.sendMessage(chatMsg);
						}
					}
				}
			}			
		});
	}
}