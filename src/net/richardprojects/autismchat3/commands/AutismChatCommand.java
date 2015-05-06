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
	
	@Override
	public boolean onCommand(final CommandSender s, Command arg1, String arg2,
			String[] args) {
		if(args.length == 0) {
			s.sendMessage(Utils.colorCodes("&3&m================&9&l AutismChat &3&m================"));
			s.sendMessage(Utils.colorCodes("&7/white &f- Set your status to white"));
			s.sendMessage(Utils.colorCodes("&7/yellow &f- Set your status to yellow"));
			s.sendMessage(Utils.colorCodes("&7/yellow add <player> &f- Add a player to your yellow list"));
			s.sendMessage(Utils.colorCodes("&7/yellow remove &f- Remove a player from your yellow list"));
			s.sendMessage(Utils.colorCodes("&7/green &f- Set your status to green"));
			s.sendMessage(Utils.colorCodes("&7/red &f- Set your status to red"));
			s.sendMessage(Utils.colorCodes("&7/join <player> &f- Join that player's party"));
			s.sendMessage(Utils.colorCodes("&7/leave &f- Leave your current party"));
			s.sendMessage(Utils.colorCodes("&7/gc (<on|off>)&f- Toggle your global chat"));
			s.sendMessage(Utils.colorCodes("&7/msg &8player &f- Send a private message"));
			s.sendMessage(Utils.colorCodes("&4/autismchat reload &c- Reload the plugin"));
			return true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("reload")) {
				if(s.hasPermission("autismchat.admin.reload")) {
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
						@Override
						public void run() {
							plugin.reloadPlugin();
							s.sendMessage(Utils.colorCodes(Messages.prefix_Good + "&7Configuration Reloaded!"));						
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
