package net.richardprojects.autismchat3.events;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Config;
import net.richardprojects.autismchat3.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {
	
	private AutismChat3 plugin;
	
	public LeaveEvent(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void leaveEvent(PlayerQuitEvent e) {
		if(Config.redHidesLoginNotification) {
			String msg = e.getQuitMessage();
			e.setQuitMessage("");
			
			for(Player p : plugin.getServer().getOnlinePlayers()) {
				if(PlayerData.getPlayerColor(p.getUniqueId()) == Color.RED) {

				} else {
					p.sendMessage(msg);
				}
			}
		}		
	}
}
