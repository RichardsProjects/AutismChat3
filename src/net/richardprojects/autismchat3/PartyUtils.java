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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PartyUtils {

	public static int createParty(String name, UUID uuid) throws Exception {
		// Loop through all the party elements get their id and determine the
		// highest value
		int highestId = 0;
		int nextId;
		Node n = null;
		File folder = new File(AutismChat3.dataFolder + File.separator
				+ "parties");
		for (File file : folder.listFiles()) {
			int currentId;
			try {
				String fileName = file.getName();
				fileName = fileName.replace(".xml", "");
				currentId = Integer.parseInt(fileName);
				if (currentId > highestId) {
					highestId = currentId;
				}
			} catch (NumberFormatException e) {

			}
		}

		nextId = highestId + 1;

		File xml = new File(AutismChat3.dataFolder + File.separator
				+ "parties" + File.separator + nextId + ".xml");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// New Party Element
		Element party = doc.createElement("party");

		// Add the members node to the party node
		Element members = doc.createElement("members");
		party.appendChild(members);

		// Add the member element
		Element member = doc.createElement("member");
		member.setTextContent(uuid.toString() + "");
		members.appendChild(member);

		// Add the party element to the end document
		doc.appendChild(party);

		// Save
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xml);
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.transform(source, result);
		return nextId;
	}

	public static boolean joinParty(AutismChat3 plugin, int partyId, UUID player) {
		boolean result = false;
		//Update the member list in the party
		File xml = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + partyId + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				Node memberNode = doc.createElement("member");
				memberNode.setTextContent(player.toString());
				
				NodeList nList = doc.getElementsByTagName("members");
				nList.item(0).appendChild(memberNode);
				
				//Save
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult sResult = new StreamResult(xml);
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
				transformer.transform(source, sResult);
				result = true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			return result;
		}		
		
		// update the player's partyId
		ACPlayer acPlayer = plugin.getACPlayer(player);
		acPlayer.setPartyId(partyId);
		result = true;
		
		return result;
	}
	
	public static List<UUID> partyMembers(int partyID) {
		List<UUID> partyMembers = new ArrayList<UUID>();
		File xml = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + partyID + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("member");
				for(int i = 0; i < nList.getLength(); i++) {
					Node currentNode = nList.item(i);
					UUID uuid = UUID.fromString(currentNode.getTextContent());
					partyMembers.add(uuid);
				}
				
				return partyMembers;
				
			} catch(Exception e) {
				e.printStackTrace();
				return partyMembers;
			}
		} else {
			return partyMembers;
		}
	}
	
	public static void removePlayerParty(int partyId, UUID player) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + partyId + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("member");
				for(int i = 0; i < nList.getLength(); i++) {
					UUID uuid = UUID.fromString(nList.item(i).getTextContent());
					if(uuid.equals(player)) {
						Node node = nList.item(i);
						node.getParentNode().removeChild(node);
					}
				}
				
				// save XML
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(xml);
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
				transformer.transform(source, result);				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	public static boolean partyExists(int partyId) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "parties" + File.separator + partyId + ".xml");
		if(xml.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
