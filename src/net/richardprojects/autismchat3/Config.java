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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

public class Config {

	public static ChatColor white;
	public static ChatColor red;
	public static ChatColor green;
	public static ChatColor yellow;
	public static int templatePartyID;
	public static Color templateColor;
	public static boolean templateGlobalChat;
	public static boolean templateMotd;
	public static boolean displayLoginMesagesToRedPlayers;
	public static boolean redHidesLoginNotification = false;
	public static boolean loginReport = true;
	public static String loginReportStatuses;
	public static String statusStatuses;
	public static String yellowStatuses;
	
	private static YamlConfiguration config;
	
	public static void setupConfig(AutismChat3 plugin) {
		try {
			PrintWriter out = new PrintWriter(plugin.config);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					AutismChat3.class.getResourceAsStream("/sampleconfig.yml")));

			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				out.append(currentLine + "\n");
			}
			out.close();
			reader.close();
			
			config = new YamlConfiguration();
			config.load(plugin.config);
		} catch (Exception e) {
			Log.info(" There was an error setting up the config file...");
			e.printStackTrace();
		}
	}

	public static void loadValues(AutismChat3 plugin) {
		//Load in case it wasn't done already
		try {
			config = new YamlConfiguration();
			config.load(plugin.config);
	
			displayLoginMesagesToRedPlayers = !config.getBoolean("redhidesloginnotify");
			
			redHidesLoginNotification = config.getBoolean("redhidesloginnotify");
			loginReport = config.getBoolean("showLoginReport");
						
			templatePartyID = config.getInt("templateForFirstLogin.partyID");
			templateColor = Color.parseString(config.getString("templateForFirstLogin.color"));
			templateGlobalChat = config.getBoolean("templateForFirstLogin.globalChat");
			templateMotd = config.getBoolean("templateForFirstLogin.displayMotd");
			
			// load status information
			loginReportStatuses = config.getString("loginReport");
			statusStatuses = config.getString("status");
			yellowStatuses = config.getString("yellowListCommand");
		} catch(Exception e) {
			AutismChat3.log.info("There was an error while loading data from the configuration...");
			e.printStackTrace();
		}
	}
}