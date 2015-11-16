package net.richardprojects.autismchat3.commands;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AutismChatCommand implements CommandExecutor {
private AutismChat3 plugin;
	
	public AutismChatCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(final CommandSender s, Command arg1, String arg2,
			String[] args) {
		if(args.length == 0) {
			for(String value : Messages.help) {
				s.sendMessage(Utils.colorCodes(value));
			}
			return true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("reload")) {
				if(s.hasPermission("autismchat.admin.reload")) {
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
			} else {
				s.sendMessage(Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs));
				return false;
			}
		} else {
			return true;
		}
		
	}
}
