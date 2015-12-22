package net.richardprojects.autismchat3.commands;

import net.md_5.bungee.api.ChatColor;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Config;
import net.richardprojects.autismchat3.Messages;
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
	
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			final String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			
			if(args.length > 0) {
				if(args.length > 1) return false;
				
				if(plugin.getUUID(args[0]) == null) {
					String msg = Messages.prefix_Bad + Messages.error_notValidPlayer;
					msg = msg.replace("{TARGET}", args[0]);
					player.sendMessage(Utils.colorCodes(msg));
				} else {
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

						public void run() {
							//Send Statuses for status
							String[] statuses = Config.statusStatuses.split(",");
							for(int i = 0; i < statuses.length; i++) {
								Utils.sendStatus(statuses[i], player, plugin.getUUID(args[0]), plugin);
							}					
						}
					});
				}
			} else {
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

					public void run() {
						//Send Statuses for status
						String[] statuses = Config.statusStatuses.split(",");
						for(int i = 0; i < statuses.length; i++) {
							Utils.sendStatus(statuses[i], player.getUniqueId(), plugin);
						}					
					}
				});
			}
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}		
		return true;
	}
}