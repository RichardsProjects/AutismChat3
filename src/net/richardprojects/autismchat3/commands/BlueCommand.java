package net.richardprojects.autismchat3.commands;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PlayerData;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class BlueCommand implements CommandExecutor {

	private AutismChat3 plugin;
	
	public BlueCommand(AutismChat3 plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			if(player.hasPermission("autismchat.admin.blue")) {
				if(args.length == 0)
				{
					PlayerData.setColor(player.getUniqueId(), Color.BLUE);
					String msg = Utils.colorCodes(Messages.prefix_Good + Messages.message_setBlue);
					player.sendMessage(msg);
					Team playerTeam = AutismChat3.board.getPlayerTeam(player);
					if(playerTeam != null) {
						String name = playerTeam.getName();
						if(name.equals("greenTeam")) {
							AutismChat3.greenTeam.removePlayer(player);
						} else if(name.equals("yellowTeam")) {
							AutismChat3.yellowTeam.removePlayer(player);
						} else if(name.equals("redTeam")) {
							AutismChat3.redTeam.removePlayer(player);
						} else if(name.equals("blueTeam")) {
							AutismChat3.blueTeam.removePlayer(player);
						}
					}
					AutismChat3.blueTeam.addPlayer(player);
					for(Player cPlayer : plugin.getServer().getOnlinePlayers()) {
						cPlayer.setScoreboard(AutismChat3.board);
					}
				} else {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				}
			} else {
				String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_noPermission);
				player.sendMessage(msg);
			}
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}		
		return true;
	}
}