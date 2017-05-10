package net.richardprojects.autismchat3.commands;

import net.richardprojects.autismchat3.ACPlayer;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutismChatCommand implements CommandExecutor {
private AutismChat3 plugin;
	
	public AutismChatCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(final CommandSender s, Command arg1, String arg2,
			String[] args) {
		if (args.length == 0) {
			for (String value : Messages.help) {
				s.sendMessage(Utils.colorCodes(value));
			}
			return true;
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (s.hasPermission("autismchat.admin.reload")) {
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
						public void run() {
							plugin.reloadPlugin();
							s.sendMessage(Utils.colorCodes(Messages.prefix_Good + Messages.message_reload));						
						}					
					});
				} else {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_noPermission);
					s.sendMessage(msg);
				}
				return true;
			} else if (args[0].equals("motd"))  {
				
				if (!(s instanceof Player)) {
					s.sendMessage("Only a player can execute this command.");
					return true;
				}
				
				Player player = (Player) s;
				
				ACPlayer acPlayer = plugin.getACPlayer(player.getUniqueId());
				if (acPlayer.getDispalyMotd()) {
					acPlayer.setDisplayMotd(false);
					String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_motdOff);
					player.sendMessage(msg);
				} else {
					acPlayer.setDisplayMotd(true);
					String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_motdOn);
					player.sendMessage(msg);
				}
				
				return true;
			} else {
				s.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
				return false;
			}
		} else if (args.length == 2) {
			if (args[0].equals("motd"))  {
				
				if (!(s instanceof Player)) {
					s.sendMessage("Only a player can execute this command.");
					return true;
				}
				
				Player player = (Player) s;
				
				if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
					if (args[1].equalsIgnoreCase("on")) {
						plugin.getACPlayer(player.getUniqueId()).setDisplayMotd(true);
						String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_motdOn);
						player.sendMessage(msg);
					} else if (args[1].equalsIgnoreCase("off")) {
						plugin.getACPlayer(player.getUniqueId()).setDisplayMotd(false);
						String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_motdOff);
						player.sendMessage(msg);
					}
					
					return true;
				} else {
					s.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
					return false;
				}
			} else {
				s.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
				return false;
			}
		} else {
			return true;
		}
	}
}
