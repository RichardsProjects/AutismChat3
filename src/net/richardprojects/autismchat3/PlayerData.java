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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PlayerData {
	
	public static void newUser(String name, UUID uuid, int partyID) {
		try {
			File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			//New user Element
			Element user = doc.createElement("user");
			
			//Add party element
			Element party = doc.createElement("party");
			party.setTextContent("" + partyID);
			user.appendChild(party);
			
			//Add the color element
			Element color = doc.createElement("color");
			color.setTextContent(Color.toString(Config.template_color));
			user.appendChild(color);
			
			//Add the globalchat element
			Element globalchat = doc.createElement("globalchat");
			String value = "true";
			if(!Config.template_globalChat) value = "false";
			globalchat.setTextContent(value);
			user.appendChild(globalchat);
			
			//Add the yellow list
			Element yellowList = doc.createElement("yellowlist");
			//Add the members in the yellowlist in the config
			/*String[] yellowListTemplate = Config.template_yellowList;
			if(yellowListTemplate.length > 0) {
				for(String member : yellowListTemplate) {
					UUID memberUUID = 
					Node node = doc.createElement("member");
					node.
				}
			}*/
			user.appendChild(yellowList);
			
			//Add the party element to the end document`
			doc.appendChild(user);
			
			//Save
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xml);
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.transform(source, result);
		} catch(Exception e) {
			Log.info("An error occured while creating a new user...");
			e.printStackTrace();
		}
	}

	public static void setColor(UUID uuid, Color color) {
		String colorString = Color.toString(color);
		
		try {
			File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();			
			Document doc = docBuilder.parse(xml);
			
			//Set the color field of the of the user
			NodeList nList = doc.getElementsByTagName("color");
			Node tempNode = nList.item(0);
			tempNode.setTextContent(colorString);
					
			//Save
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xml);
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.transform(source, result);			
		} catch(Exception e) {
			
		}
	}
	
	public static boolean playerExist(UUID uuid) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
		return xml.exists();
	}
	
	public static boolean globalChatEnabled(UUID uuid) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
		if(!xml.exists()) {
			return Config.template_globalChat;
		} else {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("globalchat");
				boolean value = Boolean.parseBoolean(nList.item(0).getTextContent());
				return value;				
			} catch (Exception e) {
				e.printStackTrace();
				return Config.template_globalChat;
			}			
		}
	}
	
	public static Color getPlayerColor(UUID uuid) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
		if(!xml.exists()) {
			return Config.template_color;
		} else {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("color");
				Color value = Color.parseString(nList.item(0).getTextContent());
				return value;				
			} catch (Exception e) {
				e.printStackTrace();
				return Config.template_color;
			}			
		}
	}
	
	public static void setGlobalChatEnabled(UUID uuid, boolean value) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("globalchat");
				String text = "true";
				if(!value) text = "false";
				nList.item(0).setTextContent(text);
				
				//Save XML
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
	
	public static List<UUID> getYellowListMembers(UUID uuid) {
		List<UUID> yellowListMembers = new ArrayList<UUID>();
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("member");
				for(int i = 0; i < nList.getLength(); i++) {
					String uuidString = nList.item(i).getTextContent();
					if(uuidString != null && !uuidString.equals("")) {
						UUID currentUUID = UUID.fromString(uuidString);
						yellowListMembers.add(currentUUID);
					}
				}
				return yellowListMembers;
				
			} catch (Exception e) {
				e.printStackTrace();
				return yellowListMembers;
			}			
		} else {
			return yellowListMembers;
		}
	}
	
	public static void addPlayerToYellowList(UUID player1, UUID addedPlayer) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + player1.toString() + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("yellowlist");
				Node node = nList.item(0);
				
				//Create member node
				Node newNode = doc.createElement("member");
				newNode.setTextContent(addedPlayer.toString());
				
				//Add Node
				node.appendChild(newNode);
				
				//Save XML
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
	
	public static void removePlayerFromYellowList(UUID player1, UUID remove) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + player1.toString() + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("member");
				for(int i = 0; i < nList.getLength(); i++) {
					UUID uuid = UUID.fromString(nList.item(i).getTextContent());
					System.out.println(uuid.toString());
					if(uuid.equals(remove)) {
						System.out.println("test1");
						Node node = nList.item(i);
						node.getParentNode().removeChild(node);
					}
				}
				
				//Save XML
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
	
	public static int getPartyID(UUID uuid) {
		File xml = new File(AutismChat3.dataFolder + File.separator + "userdata" + File.separator + uuid.toString() + ".xml");
		if(xml.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xml);
				
				NodeList nList = doc.getElementsByTagName("party");
				int partyID = Integer.parseInt(nList.item(0).getTextContent());
				
				return partyID;
				
			} catch(Exception e) {
				e.printStackTrace();
				return 0;
			}
		} else {
			return 0;
		}
	}
}