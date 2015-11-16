package net.richardprojects.autismchat3.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class AchievementEvent implements Listener {

		public AchievementEvent() {}

		@EventHandler(priority = EventPriority.MONITOR)
		public void achievementEvent(final PlayerAchievementAwardedEvent e) {
			
		}
}
