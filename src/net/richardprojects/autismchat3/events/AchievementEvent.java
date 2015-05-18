package net.richardprojects.autismchat3.events;

import net.richardprojects.autismchat3.AutismChat3;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class AchievementEvent implements Listener {

		private AutismChat3 plugin;

		public AchievementEvent(AutismChat3 plugin) {
			this.plugin = plugin;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void achievementEvent(final PlayerAchievementAwardedEvent e) {
			
		}
}
