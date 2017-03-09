package net.richardprojects.autismchat3.commands;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GcCommand implements CommandExecutor {
	
	private AutismChat3 plugin;
	
	public GcCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
					if(args[0].equalsIgnoreCase("on")) {
						plugin.getACPlayer(player.getUniqueId()).setGlobalChat(true);
						String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_globalChatOn);
						player.sendMessage(msg);
					} else if(args[0].equalsIgnoreCase("off")) {
						plugin.getACPlayer(player.getUniqueId()).setGlobalChat(false);
						String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_globalChatOff);
						player.sendMessage(msg);
					}
					return true;
				} else {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				}
			} else {
				String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
				player.sendMessage(msg);
				return false;
			}
		} else {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
	}
}
