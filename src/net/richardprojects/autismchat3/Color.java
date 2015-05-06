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

import net.md_5.bungee.api.ChatColor;

public enum Color {
	YELLOW, BLUE, RED, WHITE, GREEN;

	public static Color parseString(String string) {
		if(string.equalsIgnoreCase("yellow")) {
			return Color.YELLOW;
		} else if(string.equalsIgnoreCase("blue")) {
			return Color.BLUE;
		} else if(string.equalsIgnoreCase("red")) {
			return Color.RED;
		} else if(string.equalsIgnoreCase("white")) {
			return Color.WHITE;
		} else if(string.equalsIgnoreCase("green")) {
			return Color.GREEN;
		} else {
			return null;
		}
	}
	
	public static String toString(Color color) {
		if(color == Color.YELLOW) {
			return "YELLOW";
		} else if(color == Color.BLUE) {
			return "BLUE";
		} else if(color == Color.GREEN) {
			return "GREEN";
		} else if(color == Color.RED) {
			return "RED";
		} else if(color == Color.WHITE) {
			return "WHITE";
		} else {
			return null;
		}
	}
	
	public static String colorCode(Color color) {
		if(color == Color.YELLOW) {
			return ChatColor.GOLD + "";
		} else if(color == Color.BLUE) {
			return ChatColor.DARK_AQUA + "";
		} else if(color == Color.GREEN) {
			return ChatColor.GREEN + "";
		} else if(color == Color.RED) {
			return ChatColor.DARK_RED + "";
		} else if(color == Color.WHITE) {
			return ChatColor.WHITE + "";
		} else {
			return null;
		}
	}
}
