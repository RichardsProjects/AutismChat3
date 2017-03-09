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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveDataTask extends BukkitRunnable {

	private AutismChat3 plugin;
	private boolean flag;
	
	public SaveDataTask(AutismChat3 plugin, boolean flag) {
		this.plugin = plugin;
		this.flag = flag;
	}
	
	public void run() {
		try {
			save(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void save(boolean flag) throws Exception {		
		// loop through all the players and save them if needed
		for (Object uuid : plugin.players.keySet().toArray()) {
		    ACPlayer player = plugin.getACPlayer((UUID) uuid);
		    if (player != null) {
		    	if (player.needsUpdate || flag) {
		    		player.save(plugin);
		    	}
		    }
		}		
	}

}
