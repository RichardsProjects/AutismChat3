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
	
	public HashMap<UUID, ACPlayer> players = new HashMap<UUID, ACPlayer>();
	
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
		
		// save player data every 30 seconds if it has changed
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
				Config.setupConfig();
			} catch (Exception e) {
				log.info("Error occurred while creating config.yml file... Please check your permissions.");
			}
		}
		Config.loadValues();
		
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
	
	public boolean createNewPlayer(UUID uuid, int partyId) {
		ACPlayer acPlayer = new ACPlayer(uuid, partyId);
		players.put(uuid, acPlayer);
		return true;
	}

}
