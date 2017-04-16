/*   This file is part of AutismChat3.
*
*    AutismChat3 is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License.
*
*    You can view a copy of the GNU General Public License below
*    http://www.gnu.org/licenses/
*/

package net.richardprojects.autismchat3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import net.richardprojects.autismchat3.commands.AutismChatCommand;
import net.richardprojects.autismchat3.commands.BlueCommand;
import net.richardprojects.autismchat3.commands.GcCommand;
import net.richardprojects.autismchat3.commands.GlobalMessageCommand;
import net.richardprojects.autismchat3.commands.GreenCommand;
import net.richardprojects.autismchat3.commands.JoinCommand;
import net.richardprojects.autismchat3.commands.LeaveCommand;
import net.richardprojects.autismchat3.commands.MeCommand;
import net.richardprojects.autismchat3.commands.PrivateMessageCommands;
import net.richardprojects.autismchat3.commands.RedCommand;
import net.richardprojects.autismchat3.commands.StatusCommand;
import net.richardprojects.autismchat3.commands.WhiteCommand;
import net.richardprojects.autismchat3.commands.YellowCommand;
import net.richardprojects.autismchat3.events.ChatEvent;
import net.richardprojects.autismchat3.events.LeaveEvent;
import net.richardprojects.autismchat3.events.LoginEvent;
import net.richardprojects.autismchat3.events.PDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class AutismChat3 extends JavaPlugin {
	
	public static Logger log;
	public PluginManager pm;
	public static File config;
	public static File messages;
	public static File dataFolder;
	
	public static Scoreboard board;
	public static Team yellowTeam;
	public static Team blueTeam;
	public static Team redTeam;
	public static Team greenTeam;
	
	private final UUIDs uuids = new UUIDs(this);
	private ScoreboardManager manager;
	
	private HashMap<UUID, ACPlayer> players = new HashMap<>();
	private HashMap<Integer, ACParty> parties = new HashMap<>();
	
	private SaveDataTask saveDataTask;
	
	public void onEnable() {
		// initialize Everything
		dataFolder = getDataFolder();
		pm = this.getServer().getPluginManager();
		log = this.getLogger();
		uuids.load("uuids.yml");
		
		// check files and register everything
		checkFiles();
		registerEvents();
		registerCommands();
		
		// setup teams
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		yellowTeam = board.registerNewTeam("yellowTeam");
		yellowTeam.setPrefix(Utils.colorCodes(Messages.color_yellow));
		blueTeam = board.registerNewTeam("blueTeam");
		blueTeam.setPrefix(Utils.colorCodes(Messages.color_blue));
		redTeam = board.registerNewTeam("redTeam");
		redTeam.setPrefix(Utils.colorCodes(Messages.color_red));
		greenTeam = board.registerNewTeam("greenTeam");
		greenTeam.setPrefix(Utils.colorCodes(Messages.color_green));
		
		loadPlayerData();
		loadPartyData();
		
		// save player & party data every 30 seconds if it has changed
		saveDataTask = new SaveDataTask(this, false);
		saveDataTask.runTaskTimerAsynchronously(this, 600, 600);
	}
	
	public void onDisable() {
		log.info("[AutismChat3] Saving data to disk...");
		saveDataTask.cancel();
		saveDataTask = new SaveDataTask(this, true);
		saveDataTask.run();
	}
	
	private void checkFiles() {
		this.config = new File(dataFolder + File.separator + "config.yml");
		this.messages = new File(dataFolder + File.separator + "messages.yml");
		
		if (!(getDataFolder().exists())) {
			// Create the data folder
			getDataFolder().mkdirs();
		}		
	
		if(!config.exists()) {
			try {
				config.createNewFile();
				log.info("Created config.yml file...");
				Config.setupConfig(this);
			} catch (Exception e) {
				log.info("Error occurred while creating config.yml file... Please check your permissions.");
			}
		}
		Config.loadValues(this);
		
		if(!messages.exists()) {
			try {
				messages.createNewFile();
				log.info("Created messages.yml file...");
				Messages.setupMessages();
			} catch(Exception e) {
				log.info("Error occurred while creating messages.yml file... Please check your permissions.");
			}
		}
		Messages.loadMessages();
		
		setupSettings();
		setupParties();
	}

	/**
	 * Loads player data from yml files.
	 */
	public void loadPlayerData() {
		File[] playerDataFiles = new File(getDataFolder().getAbsolutePath() + File.separator + "userdata").listFiles();
		for(File playerFile : playerDataFiles) {
			try {
				if (playerFile.getName().endsWith(".yml")) {
					FileConfiguration playerConfig = new YamlConfiguration();
					playerConfig.load(playerFile);					
					
					int partyId = playerConfig.getInt("partyId");
					List<UUID> yellowList = Utils.convertStringToList((String) playerConfig.get("yellowList"));
					boolean globalChat = playerConfig.getBoolean("globalChat");
					Color color = Color.parseString(playerConfig.getString("color"));
					
					// uuid
					String strUUID = playerFile.getName();
					strUUID = strUUID.replace(".yml", "");
					UUID uuid = UUID.fromString(strUUID);
					
					ACPlayer p = new ACPlayer(uuid, partyId, color, globalChat, (ArrayList<UUID>) yellowList);
					players.put(uuid, p);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("[AutismChat3] There was a problem reading " + playerFile.getName() + ". Skipping...");
			}
		}
	}
	
	/**
	 * Loads party data from yml files.
	 */
	public void loadPartyData() {
		File[] partyDataFiles = new File(getDataFolder().getAbsolutePath() + File.separator + "parties").listFiles();
		for(File partyFile : partyDataFiles) {
			try {
				if (partyFile.getName().endsWith(".yml")) {
					FileConfiguration partyConfig = new YamlConfiguration();
					partyConfig.load(partyFile);					
					
					int partyId = Integer.parseInt(partyFile.getName().replaceAll(".yml", ""));
					List<UUID> memberList = Utils.convertStringToList((String) partyConfig.get("members"));
										
					ACParty p = new ACParty(memberList, partyId);
					parties.put(partyId, p);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("[AutismChat3] There was a problem reading " + partyFile.getName() + ". Skipping...");
			}
		}
	}
	
	private void setupSettings() {
		File file = new File(dataFolder + File.separator + "userdata");
		if(!file.exists()) file.mkdirs();
	}
	
	private void setupParties() {
		File file = new File(dataFolder + File.separator + "parties");
		if(!file.exists()) file.mkdirs();
	}
	
	private void registerEvents() {
		pm.registerEvents(new LoginEvent(this), this);
		pm.registerEvents(new ChatEvent(this), this);
		pm.registerEvents(new PDeathEvent(this), this);
		pm.registerEvents(new LeaveEvent(this), this);
	}
	
	private void registerCommands() {
		getCommand("leave").setExecutor(new LeaveCommand(this));
		getCommand("yellow").setExecutor(new YellowCommand(this));
		getCommand("global").setExecutor(new GlobalMessageCommand(this));
		getCommand("gc").setExecutor(new GcCommand(this));
		getCommand("white").setExecutor(new WhiteCommand(this));
		getCommand("red").setExecutor(new RedCommand(this));
		getCommand("blue").setExecutor(new BlueCommand(this));
		getCommand("green").setExecutor(new GreenCommand(this));
		getCommand("join").setExecutor(new JoinCommand(this));
		getCommand("autismchat").setExecutor(new AutismChatCommand(this));
		getCommand("status").setExecutor(new StatusCommand(this));
		
		//Override Vanilla private message commands
		getCommand("w").setExecutor(new PrivateMessageCommands(this));
		getCommand("tell").setExecutor(new PrivateMessageCommands(this));
		getCommand("msg").setExecutor(new PrivateMessageCommands(this));
		
		//Override Vanilla me commmand
		getCommand("me").setExecutor(new MeCommand(this));
	}
	
	public void updateUUID(String name, UUID uuid) {
		boolean keyNeedsToBeUpdated = false;
		String oldUsername = "";
		for(String key : uuids.getConfig().getKeys(true)) {
			UUID keyUUID = UUID.fromString(uuids.getConfig().getString(key));
			if(uuid.equals(keyUUID)) {
				keyNeedsToBeUpdated = true;
				oldUsername = key;
			}
		}
		if(keyNeedsToBeUpdated) {
			uuids.getConfig().set(oldUsername, null);
		}
		uuids.getConfig().set(name, uuid.toString());
		uuids.save();
	}
	
	public String getName(UUID uuid) {
		String playerName = null;
		for(String key : uuids.getConfig().getKeys(true)) {
			UUID keyUUID = UUID.fromString(uuids.getConfig().getString(key));
			if(uuid.equals(keyUUID)) {
				playerName = key;
				return playerName;
			}
		}
		return playerName;
	}
	
	public UUID getUUID(String name) {
		for(String key : uuids.getConfig().getKeys(true)) {
			if(key.equalsIgnoreCase(name)) {
				UUID keyUUID = UUID.fromString(uuids.getConfig().getString(key));
				return keyUUID;
			}
		}
		return null;
	}
	
	public void reloadPlugin() {
		checkFiles();
	}
	
	/**
	 * Gets an ACPlayer from the players list if they exist. Otherwise the 
	 * method returns null.
	 * 
	 * @param uuid the of the player to get 
	 * @return the ACPlayer or null
	 */
	public ACPlayer getACPlayer(UUID uuid) {
		if (players.containsKey(uuid)) {
			return players.get(uuid);
		} else {
			return null;
		}
	}
	
	/**
	 * Instantiates a new ACPlayer and adds it to the players HashMap.
	 * 
	 * @param uuid player's UUID
	 * @param partyId the id of the party the player belongs to
	 */
	public void createNewPlayer(UUID uuid, int partyId) {
		ACPlayer acPlayer = new ACPlayer(uuid, partyId);
		players.put(uuid, acPlayer);
	}
	
	/**
	 * Gets an ACParty from the parties list if it exists. Otherwise the method
	 * returns null.
	 * 
	 * @param id the party's id
	 * @return the ACParty object or null if none
	 */
	public ACParty getACParty(int id) {
		if (parties.containsKey(id)) {
			return parties.get(id);
		} else {
			return null;
		}
	}
	
	/**
	 * Instantiates a new ACParty and adds it to the parties HashMap.
	 * 
	 * @param firstMember the party's first member's uuid
	 * @return the new party's id
	 */
	public int createNewParty(UUID firstMember) {
		ACParty acParty = new ACParty(firstMember, this);
		parties.put(acParty.getId(), acParty);
		return acParty.getId();
	}
	
	/**
	 * Returns a list of all the UUIDs of the current players.
	 * 
	 * @return current ACPlayer's UUIDs
	 */
	public ArrayList<UUID> playersUUIDs() {
		return new ArrayList<UUID>(players.keySet());
	}
	
	/**
	 * Returns a list of all the IDs of the current parties.
	 * 
	 * @return current party IDs
	 */
	public ArrayList<Integer> partyIDs() {
		return new ArrayList<Integer>(parties.keySet());
	}
	
	/**
	 * Deletes the specified party from disk and removes it from the HashMap
	 * 
	 * @param partyId the id of the party to delete
	 */
	public void deleteParty(int partyId) {
		if (parties.containsKey(partyId)) {
			parties.remove(partyId);
		}
		
		String path = dataFolder + File.separator + "parties" + File.separator + partyId + ".yml";
		File party = new File(path);
		if (party.exists()) {
			party.delete();
		}
	}
	
	/**
	 * Adds a player to an existing party's member list and sets their party 
	 * id to match the specified party.
	 * 
	 * @param partyId id of the party to join
	 * @param player player's UUID
	 * @return
	 */
	public boolean joinParty(int partyId, UUID player) {
		ACPlayer acPlayer = getACPlayer(player);
		if (acPlayer == null) return false;
		
		// have player leave their current party
		int currentPartyId = acPlayer.getPartyId();	
		ACParty oldParty = getACParty(currentPartyId);
		if (oldParty != null) {
			
			// notify everyone in the party that the player left
			for (UUID playerId : oldParty.getMembers()) {
				Player cPlayer = getServer().getPlayer(playerId);
				
				if (cPlayer != null) {
					String msg = "";
					
					if (!playerId.equals(player)) {
						msg = Messages.message_leaveParty;
						String pName = Utils.formatName(this, player, cPlayer.getUniqueId());
						msg = msg.replace("{PLAYER}", pName);
						msg = msg.replace("{PLAYERS} {REASON}", Messages.reasonJoinedAnotherParty);
					} else {
						msg = Messages.message_youLeaveParty;
						String partyMemberList = Utils.partyMembersString(this, currentPartyId, player);
						msg = msg.replace("{PLAYERS} {REASON}", partyMemberList);
					}
					
					cPlayer.sendMessage(Utils.colorCodes(msg));
				}
			}	
			
			// remove the player and delete the party
			oldParty.removeMember(player);			
			if (oldParty.getMembers().isEmpty()) {
				// delete party because it is empty
				deleteParty(currentPartyId);
			}
		}
		
		// add member to the specified party
		ACParty newParty = getACParty(partyId);
		if (newParty != null) {
			newParty.addMember(player);
			
			for (UUID member : newParty.getMembers()) {
				Player cPlayer = getServer().getPlayer(member);
							
				if (cPlayer != null) {
					String msg = "";
					
					if (!member.equals(player)) {
						// send join message to member
						msg = Messages.message_joinParty;
						String name = Utils.formatName(this, player, member);
						msg = msg.replace("{PLAYER}", name);
						msg = msg.replace(" {MEMBERS}", "");
					} else {
						String partyMemberList = Utils.partyMembersString(this, partyId, member);
						msg = Messages.message_youJoinParty;
						msg = msg.replace("has", "have");
						msg = msg.replace("{MEMBERS}", partyMemberList);
					}
					
					cPlayer.sendMessage(Utils.colorCodes(msg));
				}
			}
		} else {
			return false;
		}
		
		// update the player's partyId
		acPlayer.setPartyId(partyId);		
		
		return true;
	}
}
