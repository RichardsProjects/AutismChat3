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
					// check if player is online
					Player recipient;
					if ((recipient = this.plugin.getServer().getPlayerExact(args[0])) != null) {
						boolean sendMessage = true;

						// check if the receiving player is red
						if (plugin.getACPlayer(recipient.getUniqueId()).getColor() == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noAcceptingRed;
							String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
							msg = msg.replace("{RECEIVER}", name);
							player.sendMessage(Utils.colorCodes(msg));
							return true;
						}
						
						// check if the receiving player is yellow
						if (plugin.getACPlayer(recipient.getUniqueId()).getColor() == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = plugin.getACPlayer(recipient.getUniqueId()).getYellowList();
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
						if(plugin.getACPlayer(player.getUniqueId()).getColor() == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noSendingRed;
							player.sendMessage(Utils.colorCodes(msg));
						}
						
						//Check if the sending player is yellow
						if(plugin.getACPlayer(player.getUniqueId()).getColor() == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = plugin.getACPlayer(player.getUniqueId()).getYellowList();
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
						
						// create message
						String message = "";
						for(int i = 0; i < args.length; i++) {
							if(i != 0) message += args[i] + " ";
						}
						message = message.trim();
						
						// send message
						if (sendMessage) {
							// show message for recipient
							String msg1 = Messages.prefix_MessageReceiving + message;
							msg1 = msg1.replace("PLAYER", Color.colorCode(plugin.getACPlayer(player.getUniqueId()).getColor()) + player.getName());
							recipient.sendMessage(Utils.colorCodes(msg1));
							
							// show message for sender
							String msg2 = Messages.prefix_MessageSending + message;
							msg2 = msg2.replace("PLAYER", Color.colorCode(plugin.getACPlayer(player.getUniqueId()).getColor()) + recipient.getName());
							player.sendMessage(Utils.colorCodes(msg2));							
						}
					} else {
						// return an error message
						player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + " The specified player is not online."));
					}
				}
			} else if(cmd.getName().equalsIgnoreCase("tell")) {
				// tell command
				if(args.length < 2) {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				} else {
					// check if player is online
					Player recipient;
					if((recipient = this.plugin.getServer().getPlayerExact(args[0])) != null) {
						boolean sendMessage = true;

						// check if the receiving player is red
						if (plugin.getACPlayer(recipient.getUniqueId()).getColor() == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noAcceptingRed;
							String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
							msg = msg.replace("{RECEIVER}", name);
							player.sendMessage(Utils.colorCodes(msg));
							return true;
						}
						
						// check if the receiving player is yellow
						if (plugin.getACPlayer(recipient.getUniqueId()).getColor() == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = plugin.getACPlayer(recipient.getUniqueId()).getYellowList();
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
						if(plugin.getACPlayer(player.getUniqueId()).getColor() == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noSendingRed;
							player.sendMessage(Utils.colorCodes(msg));
						}
						
						//Check if the sending player is yellow
						if(plugin.getACPlayer(player.getUniqueId()).getColor() == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = plugin.getACPlayer(player.getUniqueId()).getYellowList();
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
						
						// create message
						String message = "";
						for(int i = 0; i < args.length; i++) {
							if(i != 0) message += args[i] + " ";
						}
						message = message.trim();
						
						// send message
						if(sendMessage) {
							// show message for recipient
							String msg1 = Messages.prefix_MessageReceiving + message;
							msg1 = msg1.replace("PLAYER", Color.colorCode(plugin.getACPlayer(player.getUniqueId()).getColor()) + player.getName());
							recipient.sendMessage(Utils.colorCodes(msg1));
							
							// show message for sender
							String msg2 = Messages.prefix_MessageSending + message;
							msg2 = msg2.replace("PLAYER", Color.colorCode(plugin.getACPlayer(player.getUniqueId()).getColor()) + recipient.getName());
							player.sendMessage(Utils.colorCodes(msg2));							
						}
					} else {
						//Return an error message
						player.sendMessage(Utils.colorCodes(Messages.prefix_Bad + " The specified player is not online."));
					}
				}
			} else if(cmd.getName().equalsIgnoreCase("w")) {
				// w command
				if(args.length < 2) {
					String msg = Utils.colorCodes(Messages.prefix_Bad + Messages.error_invalidArgs);
					player.sendMessage(msg);
					return false;
				} else {
					// check if player is online
					Player recipient;
					if((recipient = this.plugin.getServer().getPlayerExact(args[0])) != null) {
						boolean sendMessage = true;

						// check if the receiving player is red
						if(plugin.getACPlayer(recipient.getUniqueId()).getColor() == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noAcceptingRed;
							String name = Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId());
							msg = msg.replace("{RECEIVER}", name);
							player.sendMessage(Utils.colorCodes(msg));
							return true;
						}
						
						// check if the receiving player is yellow
						if(plugin.getACPlayer(recipient.getUniqueId()).getColor() == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = plugin.getACPlayer(recipient.getUniqueId()).getYellowList();
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
						
						// check if the sending player is red
						if(plugin.getACPlayer(player.getUniqueId()).getColor() == Color.RED) {
							sendMessage = false;
							String msg = Messages.prefix_Bad + Messages.error_noSendingRed;
							player.sendMessage(Utils.colorCodes(msg));
						}
						
						// check if the sending player is yellow
						if(plugin.getACPlayer(player.getUniqueId()).getColor() == Color.YELLOW) {
							sendMessage = false;
							List<UUID> uuids = plugin.getACPlayer(player.getUniqueId()).getYellowList();
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
						
						// create message
						String message = "";
						for(int i = 0; i < args.length; i++) {
							if(i != 0) message += args[i] + " ";
						}
						message = message.trim();
						
						// send message
						if(sendMessage) {
							// show message for recipient
							String msg1 = Messages.prefix_MessageReceiving + message;
							msg1 = msg1.replace("PLAYER", Utils.formatName(plugin, player.getUniqueId(), recipient.getUniqueId()));
							recipient.sendMessage(Utils.colorCodes(msg1));
							
							// show message for sender
							String msg2 = Messages.prefix_MessageSending + message;
							msg2 = msg2.replace("PLAYER", Utils.formatName(plugin, recipient.getUniqueId(), player.getUniqueId()));
							player.sendMessage(Utils.colorCodes(msg2));							
						}
					} else {
						// return an error message
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