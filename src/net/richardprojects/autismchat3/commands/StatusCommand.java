package net.richardprojects.autismchat3.commands;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Config;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {

	private AutismChat3 plugin;
	
	public StatusCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					//Send Statuses for status
					String[] statuses = Config.statusStatuses.split(",");
					for(int i = 0; i < statuses.length; i++) {
						Utils.sendStatus(statuses[i], player.getUniqueId(), plugin);
					}					
				}
			});
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}		
		return true;
	}
}