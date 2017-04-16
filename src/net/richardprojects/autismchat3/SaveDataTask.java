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

import java.util.UUID;

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
		for (UUID uuid : plugin.playersUUIDs()) {
		    ACPlayer player = plugin.getACPlayer(uuid);
		    if (player != null) {
		    	if (player.needsUpdate || flag) {
		    		player.save(plugin);
		    	}
		    }
		}
		
		// loop through all parties and save them if needed
		for (int id : plugin.partyIDs()) {
		    ACParty party = plugin.getACParty(id);
		    if (party != null) {
		    	if (party.needsUpdate || flag) {
		    		party.save(plugin);
		    	}
		    }
		}
	}

}
