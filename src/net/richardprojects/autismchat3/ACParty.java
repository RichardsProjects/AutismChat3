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
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * A simple class that represents an AutismChat3 party
 * 
 * @author RichardB122
 * @version 4/15/17
 */
public class ACParty {

	private int id;
	private ArrayList<UUID> members;
	
	public boolean needsUpdate;
	
	/**
	 * A constructor used for creating a new party for the first time.
	 * 
	 * @param firstMember the party's first member's uuid
	 * @param plugin a reference to the AutismChat3 plugin
	 */
	public ACParty(UUID firstMember, AutismChat3 plugin) {
		members = new ArrayList<>();
		members.add(firstMember);
		
		// determine the highest party id
		int highestId = 0;
		for (int partyId : plugin.partyIDs()) {
			if (partyId > highestId) {
				highestId = partyId;
			}
		}
		
		id = highestId + 1;
		needsUpdate = true;
	}
	
	/**
	 * A constructor used when party data is simply being loaded from an 
	 * existing file.
	 * 
	 * @param members list of members of the party
	 * @param id the party's id
	 */
	public ACParty(List<UUID> members, int id) {
		this.members = new ArrayList<>(members);
		this.id = id;
		this.needsUpdate = true;
	}
	
	public int getId() {
		return id;
	}
	
	public ArrayList<UUID> getMembers() {
		return members;
	}
	
	public void addMember(UUID newUUID) {
		this.members.add(newUUID);
		this.needsUpdate = true;
	}
	
	public void removeMember(UUID uuid) {
		if (members.contains(uuid)) {
			members.remove(uuid);
		}		
		this.needsUpdate = true;
	}
	
	/**
	 * Attempts to save a copy of the ACParty to disk. Returns true if 
	 * successful and false if the operation failed.
	 * 
	 * @param plugin a reference to the AutismChat3 plugin
	 * @return whether the operation was successful or not
	 */
	public boolean save(AutismChat3 plugin) {
		try {
			// load or create new file
			FileConfiguration partyFile = new YamlConfiguration();
			File file = new File(plugin.getDataFolder().toString() + File.separator + "parties" + File.separator + id + ".yml");
			if (!file.exists()) {
				boolean result = file.createNewFile();
				if (!result) return false;
			}
			partyFile.load(file);
			
			// save data
			partyFile.set("members", Utils.convertListToString(members));
			partyFile.save(file);
			
			this.needsUpdate = false;			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
