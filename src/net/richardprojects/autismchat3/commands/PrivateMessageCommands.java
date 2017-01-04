package net.richardprojects.autismchat3.commands;

import java.util.List;
import java.util.UUID;

import net.richardprojects.autismchat3.AutismChat3;
import net.richardprojects.autismchat3.Color;
import net.richardprojects.autismchat3.Messages;
import net.richardprojects.autismchat3.PlayerData;
import net.richardprojects.autismchat3.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMessageCommands implements CommandExecutor {

	private AutismChat3 plugin;
	
	public PrivateMessageCommands(AutismChat3 plugin) {
		this.plugin = plugin;		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,
			String[] args) {
		if(sender instanceof Player) {
			final Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("msg")) {
				//Msg command
				if(args.length < 2) {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				} else {
					//Check if player is online
					Player recipient;
					if((recipient = this.plugin.getServer().getPlayerExact(args[0])) != null) {
						boolean sendMessage = true;

						// check if the receiving player is red
						if(PlayerData.getPlayerColor(recipient.getUniqueId()) == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noAcceptingRed;
							String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
							msg = msg.replace("{RECEIVER}", name);
							player.sendMessage(Utils.colorCodes(msg));
							return true;
						}
						
						//Check if the receiving player is yellow
						if(PlayerData.getPlayerColor(recipient.getUniqueId()) == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = PlayerData.getYellowListMembers(recipient.getUniqueId());
							for(UUID currentUUID : uuids) {
								if(currentUUID.equals(player.getUniqueId())) {
									sendMessage = true;
								}
							}
							if(!sendMessage) {
								String msg = Messages.prefix_Bad + Messages.error_noAcceptingYellow;
								String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
								msg = msg.replace("{RECEIVER}", name);
								player.sendMessage(Utils.colorCodes(msg));
								return true;
							}
						}
						
						//Check if the sending player is red
						if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noSendingRed;
							player.sendMessage(Utils.colorCodes(msg));
						}
						
						//Check if the sending player is yellow
						if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = PlayerData.getYellowListMembers(player.getUniqueId());
							for(UUID currentUUID : uuids) {
								if(currentUUID.equals(recipient.getUniqueId())) {
									sendMessage = true;
								}
							}
							if(!sendMessage) {
								String msg = Messages.prefix_Bad + Messages.error_noSendingYellow;
								String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
								msg = msg.replace("{RECEIVER}", name);
								player.sendMessage(Utils.colorCodes(msg));
							}
						}
						
						//Create message
						String message = "";
						for(int i = 0; i < args.length; i++) {
							if(i != 0) message += args[i] + " ";
						}
						message = message.trim();
						
						//Send Message
						if(sendMessage) {
							//Show messge for recipient
							String msg1 = Messages.prefix_MessageReceiving + message;
							msg1 = msg1.replace("PLAYER", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
							recipient.sendMessage(Utils.colorCodes(msg1));
							
							//Show message for sender
							String msg2 = Messages.prefix_MessageSending + message;
							msg2 = msg2.replace("PLAYER", Color.colorCode(PlayerData.getPlayerColor(recipient.getUniqueId())) + recipient.getName());
							player.sendMessage(Utils.colorCodes(msg2));							
						}
					} else {
						//Return an error message
						player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + " The specified player is not online."));
					}
				}
			} else if(cmd.getName().equalsIgnoreCase("tell")) {
				//tell command
				//Msg command
				if(args.length < 2) {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				} else {
					//Check if player is online
					Player recipient;
					if((recipient = this.plugin.getServer().getPlayerExact(args[0])) != null) {
						boolean sendMessage = true;

						//Check if the receiving player is red
						if(PlayerData.getPlayerColor(recipient.getUniqueId()) == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noAcceptingRed;
							String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
							msg = msg.replace("{RECEIVER}", name);
							player.sendMessage(Utils.colorCodes(msg));
							return true;
						}
						
						//Check if the receiving player is yellow
						if(PlayerData.getPlayerColor(recipient.getUniqueId()) == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = PlayerData.getYellowListMembers(recipient.getUniqueId());
							for(UUID currentUUID : uuids) {
								if(currentUUID.equals(player.getUniqueId())) {
									sendMessage = true;
								}
							}
							if(!sendMessage) {
								String msg = Messages.prefix_Bad + Messages.error_noAcceptingYellow;
								String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
								msg = msg.replace("{RECEIVER}", name);
								player.sendMessage(Utils.colorCodes(msg));
								return true;
							}
						}
						
						//Check if the sending player is red
						if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noSendingRed;
							player.sendMessage(Utils.colorCodes(msg));
						}
						
						//Check if the sending player is yellow
						if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = PlayerData.getYellowListMembers(player.getUniqueId());
							for(UUID currentUUID : uuids) {
								if(currentUUID.equals(recipient.getUniqueId())) {
									sendMessage = true;
								}
							}
							if(!sendMessage) {
								String msg = Messages.prefix_Bad + Messages.error_noSendingYellow;
								String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
								msg = msg.replace("{RECEIVER}", name);
								player.sendMessage(Utils.colorCodes(msg));
							}
						}
						
						//Create message
						String message = "";
						for(int i = 0; i < args.length; i++) {
							if(i != 0) message += args[i] + " ";
						}
						message = message.trim();
						
						//Send Message
						if(sendMessage) {
							//Show messge for recipient
							String msg1 = Messages.prefix_MessageReceiving + message;
							msg1 = msg1.replace("PLAYER", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
							recipient.sendMessage(Utils.colorCodes(msg1));
							
							//Show message for sender
							String msg2 = Messages.prefix_MessageSending + message;
							msg2 = msg2.replace("PLAYER", Color.colorCode(PlayerData.getPlayerColor(recipient.getUniqueId())) + recipient.getName());
							player.sendMessage(Utils.colorCodes(msg2));							
						}
					} else {
						//Return an error message
						player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + " The specified player is not online."));
					}
				}
			} else if(cmd.getName().equalsIgnoreCase("w")) {
				//w command
				//Msg command
				if(args.length < 2) {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				} else {
					//Check if player is online
					Player recipient;
					if((recipient = this.plugin.getServer().getPlayerExact(args[0])) != null) {
						boolean sendMessage = true;

						//Check if the receiving player is red
						if(PlayerData.getPlayerColor(recipient.getUniqueId()) == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noAcceptingRed;
							String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
							msg = msg.replace("{RECEIVER}", name);
							player.sendMessage(Utils.colorCodes(msg));
							return true;
						}
						
						//Check if the receiving player is yellow
						if(PlayerData.getPlayerColor(recipient.getUniqueId()) == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = PlayerData.getYellowListMembers(recipient.getUniqueId());
							for(UUID currentUUID : uuids) {
								if(currentUUID.equals(player.getUniqueId())) {
									sendMessage = true;
								}
							}
							if(!sendMessage) {
								String msg = Messages.prefix_Bad + Messages.error_noAcceptingYellow;
								String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
								msg = msg.replace("{RECEIVER}", name);
								player.sendMessage(Utils.colorCodes(msg));
								return true;
							}
						}
						
						//Check if the sending player is red
						if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noSendingRed;
							player.sendMessage(Utils.colorCodes(msg));
						}
						
						//Check if the sending player is yellow
						if(PlayerData.getPlayerColor(player.getUniqueId()) == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = PlayerData.getYellowListMembers(player.getUniqueId());
							for(UUID currentUUID : uuids) {
								if(currentUUID.equals(recipient.getUniqueId())) {
									sendMessage = true;
								}
							}
							if(!sendMessage) {
								String msg = Messages.prefix_Bad + Messages.error_noSendingYellow;
								String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
								msg = msg.replace("{RECEIVER}", name);
								player.sendMessage(Utils.colorCodes(msg));
							}
						}
						
						//Create message
						String message = "";
						for(int i = 0; i < args.length; i++) {
							if(i != 0) message += args[i] + " ";
						}
						message = message.trim();
						
						//Send Message
						if(sendMessage) {
							//Show messge for recipient
							String msg1 = Messages.prefix_MessageReceiving + message;
							msg1 = msg1.replace("PLAYER", Color.colorCode(PlayerData.getPlayerColor(player.getUniqueId())) + player.getName());
							recipient.sendMessage(Utils.colorCodes(msg1));
							
							//Show message for sender
							String msg2 = Messages.prefix_MessageSending + message;
							msg2 = msg2.replace("PLAYER", Color.colorCode(PlayerData.getPlayerColor(recipient.getUniqueId())) + recipient.getName());
							player.sendMessage(Utils.colorCodes(msg2));							
						}
					} else {
						//Return an error message
						player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + " The specified player is not online."));
					}
				}
			}
		} else {
			sender.sendMessage("Only a player can execute this command.");
		}		
		return true;
	}

}