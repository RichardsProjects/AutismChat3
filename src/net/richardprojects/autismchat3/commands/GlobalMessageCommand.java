package net.richardprojects.autismchat3.commands;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.richardprojects.autismchat3.ACPlayer;
import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalMessageCommand implements CommandExecutor {
	private AutismChat3 plugin;
	
	public GlobalMessageCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			ACPlayer acPlayer = plugin.getACPlayer(player.getUniqueId());
			
			//Switch the players global chat on if it's off so they can see the responses to their message
			if(!acPlayer.isGlobalChatEnabled()) {
				acPlayer.setGlobalChat(true);
				String msg = Messages.prefix_Good + Messages.message_gcAutoEnabled;
				player.sendMessage(Utils.colorCodes(msg));
			}
			String playerName = Color.colorCode(acPlayer.getColor()) + player.getName();
			if(args.length == 0)
			{
				String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_noMessage);
				player.sendMessage(msg);
				return true;
			} else {
				String chatMsg = "";
				for(int i = 0; i < args.length; i++) {
					chatMsg = chatMsg + args[i] + " ";
				}
				chatMsg = chatMsg.trim();
				for(Player player2 : plugin.getServer().getOnlinePlayers()) {
					UUID uuid = player2.getUniqueId();
					if(plugin.getACPlayer(uuid).isGlobalChatEnabled()) {
						String msg = Messages.globalChatFormat;
						msg = msg.replace("%name%", playerName + ChatColor.RESET);
						msg = msg.replace("%message%", chatMsg);
						msg = Utils.colorCodes(msg);
						player2.sendMessage(msg);
					}
				}
				return true;
			}
		} else {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
	}
}
