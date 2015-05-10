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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

public class Messages {
	public static String color_red;
	public static String color_yellow;
	public static String color_blue;
	public static String color_green;
	public static String status_colorSetting;
	public static String status_yellowList;
	public static String status_globalChat;
	public static String status_partyMembers;
	public static String prefix_Bad;
	public static String prefix_Good;
	public static String prefix_Message;
	public static String error_noPermission;
	public static String error_notValidPlayer;
	public static String error_noAcceptingRed;
	public static String error_noAcceptingYellow;
	public static String error_noSendingRed;
	public static String error_noSendingYellow;
	public static String error_invalidArgs;
	public static String error_noMessage;
	public static String error_JoinParty1;
	public static String error_JoinParty2;
	public static String error_JoinParty3;
	public static String error_JoinParty4;
	public static String error_JoinParty5;
	public static String error_JoinParty6;
	public static String error_yellowNoMatch;
	public static String error_yellowDuplicate;
	public static String message_yellowAdd;
	public static String message_yellowRemove;
	public static String message_setYellow;
	public static String message_setRed;
	public static String message_setWhite;
	public static String message_setGreen;
	public static String message_setBlue;
	public static String message_globalChatOff;
	public static String message_globalChatOn;
	public static String message_yellowListAdd;
	public static String message_yellowListRemove;
	public static String message_gcAutoEnabled;
	public static String message_nobodyHeardMessage;
	public static String message_joinParty;
	public static String message_leaveParty;
	public static String message_onlyOneInParty;
	public static String message_loadingSettings;
	public static String partyChatFormat;
	public static String globalChatFormat;
	public static List<String> motd = new ArrayList<String>();
	
	private static YamlConfiguration messagesConfig;
	
	public static void setupMessages() {
		try {
			PrintWriter out = new PrintWriter(AutismChat3.messages);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					AutismChat3.class.getResourceAsStream("/samplemessages.yml")));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				out.append(currentLine + "\n");
			}
			out.close();
			reader.close();
			
			messagesConfig = new YamlConfiguration();
			messagesConfig.load(AutismChat3.messages);
		} catch (Exception e) {
			Log.info(" There was an error setting up the config file...");
			e.printStackTrace();
		}
	}
	
	public static void loadMessages() {
		try {
			messagesConfig = new YamlConfiguration();
			messagesConfig.load(AutismChat3.messages);
			
			prefix_Bad = messagesConfig.getString("prefixBad");
			prefix_Good = messagesConfig.getString("prefixGood");
			prefix_Message = messagesConfig.getString("prefixMessage");
			error_noPermission = messagesConfig.getString("errNoPerm");
			error_notValidPlayer = messagesConfig.getString("notValidPlayer");
			message_yellowAdd = messagesConfig.getString("yellowAdd");
			message_yellowRemove = messagesConfig.getString("yellowRemove");
			message_setYellow = messagesConfig.getString("statusSetYellow");
			message_setRed = messagesConfig.getString("statusSetRed");
			message_setWhite = messagesConfig.getString("statusSetWhite");
			message_setGreen = messagesConfig.getString("statusSetGreen");
			message_setBlue = messagesConfig.getString("statusSetBlue");
			message_globalChatOff = messagesConfig.getString("globalChatOff");
			message_globalChatOn = messagesConfig.getString("globalChatOn");
			error_noAcceptingRed = messagesConfig.getString("noAcceptingRed");
			error_noAcceptingYellow = messagesConfig.getString("noAcceptingYellow");
			error_noSendingRed = messagesConfig.getString("noSendingRed");
			error_noSendingYellow = messagesConfig.getString("noSendingYellow");
			error_invalidArgs = messagesConfig.getString("invalidArgs");
			error_noMessage = messagesConfig.getString("noMessageEntered");
			error_yellowDuplicate = messagesConfig.getString("yellowDuplicate");
			error_yellowNoMatch = messagesConfig.getString("yellowNoMatch");
			message_yellowListAdd = messagesConfig.getString("yellowListAdd");
			message_yellowListRemove = messagesConfig.getString("yellowListRemove");
			message_nobodyHeardMessage = messagesConfig.getString("nobodyHeardMessage");
			message_gcAutoEnabled = messagesConfig.getString("gcAutoEnabled");
			message_joinParty = messagesConfig.getString("joinParty");
			message_leaveParty = messagesConfig.getString("leaveParty");
			message_loadingSettings = messagesConfig.getString("loadingSettingsMsg");
			
			message_onlyOneInParty = messagesConfig.getString("leavingPartyWithOnlyOnePersonInIt");
			
			error_JoinParty1 = messagesConfig.getString("partyJoinErrorMessages.1");
			error_JoinParty2 = messagesConfig.getString("partyJoinErrorMessages.2");
			error_JoinParty3 = messagesConfig.getString("partyJoinErrorMessages.3");
			error_JoinParty4 = messagesConfig.getString("partyJoinErrorMessages.4");
			error_JoinParty5 = messagesConfig.getString("partyJoinErrorMessages.5");
			error_JoinParty6 = messagesConfig.getString("partyJoinErrorMessages.6");
			
			//Load Chat Formats
			partyChatFormat = messagesConfig.getString("partyChatFormat");
			globalChatFormat = messagesConfig.getString("globalChatFormat");
			
			motd = messagesConfig.getStringList("motd");
			
			//Load Colors
			color_blue = messagesConfig.getString("blue");
			color_yellow = messagesConfig.getString("yellow");
			color_red = messagesConfig.getString("red");
			color_green = messagesConfig.getString("green");
			
			//Load Status Messages
			status_colorSetting = messagesConfig.getString("status.colourSetting");
			status_yellowList = messagesConfig.getString("status.yellowList");
			status_globalChat = messagesConfig.getString("status.globalChat");
			status_partyMembers = messagesConfig.getString("status.partyMembers");
		} catch(Exception e) {
			AutismChat3.log.info("There was an error while loading data from the messages...");
			e.printStackTrace();
		}
	}
	
}
