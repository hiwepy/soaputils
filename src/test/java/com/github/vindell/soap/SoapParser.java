/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.vindell.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SoapParser {

	public static void main(String[] args) {
		doSoapPost();
	}

	public static void doSoapPost() {
		try {
			
			// First create the connection
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnFactory.createConnection();// 创建连接

			// Next, create the actual message
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();// 创建soap请求

			// Create objects for the message parts
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody body = envelope.getBody();

			// //Populate the body
			// //Create the main element and namespace
			SOAPElement bodyElement = body
					.addChildElement(envelope.createName("getFriendsList", "ns1", "http://pojo.test.com"));
			// Add content
			SOAPElement firstElemnt = bodyElement.addChildElement("in0");
			Name firstName = envelope.createName("type");
			firstElemnt.addAttribute(firstName, "int");
			firstElemnt.addTextNode("1");

			SOAPElement secondElemnt = bodyElement.addChildElement("in1");
			Name secondName = envelope.createName("type");
			secondElemnt.addAttribute(secondName, "int");
			secondElemnt.addTextNode("0");

			// Save the message
			message.saveChanges();
			// Check the input
			message.writeTo(System.out);
			System.out.println();
			// Send the message and get a reply

			// Set the destination
			String destination = "http://192.168.1.10:8080/myTest/services/MyService";
			// Send the message
			SOAPMessage reply = connection.call(message, destination);

			if (reply != null) {
				SOAPPart replySP = reply.getSOAPPart();
				SOAPEnvelope replySE = replySP.getEnvelope();
				SOAPBody replySB = replySE.getBody();

				Source source = reply.getSOAPPart().getContent();
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				ByteArrayOutputStream myOutStr = new ByteArrayOutputStream();
				StreamResult res = new StreamResult();
				res.setOutputStream(myOutStr);
				transformer.transform(source, res);
				String temp = myOutStr.toString("UTF-8");

				System.out.println(temp);
				byte[] bytes = temp.getBytes("UTF-8");
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = null;
				Document doc = null;
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
				Element docEle = doc.getDocumentElement();
				NodeList nl = docEle.getElementsByTagName("ns2:FriendsList");
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						// get the employee element
						Element el = (Element) nl.item(i);
						String name = getTextValue(el, "name");
						int id = getIntValue(el, "userId");
						System.out.println("name: " + name + " id: " + id);
					}
				}
			}
			// Close the connection
			connection.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text
	 * content i.e for <employee><name>John</name></employee> xml snippet if the
	 * Element points to employee node and tagName is name I will return John
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}
		return textVal;
	}

	/**
	 * Calls getTextValue and returns a int value
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private static int getIntValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele, tagName));
	}

	private static void parseXmlFile(String fileName) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(fileName);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
