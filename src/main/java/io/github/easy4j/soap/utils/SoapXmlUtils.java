/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
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
package io.github.easy4j.soap.utils;

import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Low-level XML node utility methods for working with SOAP DOM structures.
 * Provides helpers for element naming, sibling counting, child element
 * filtering by type, and collection detection via comment markers.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see XmlUtils
 */
public class SoapXmlUtils {
	
	/**
	 * Returns the local name of the given DOM node, falling back to the
	 * full node name if the local name is {@code null}.
	 *
	 * @param node the DOM node
	 * @return the element name
	 */
	public static String getName(Node node) {
		String name = node.getLocalName();
		if (name != null) {
			return name;
		}
		return node.getNodeName();
	}
	
	/**
	 * Returns the local name of the given DOM element, falling back to the
	 * tag name if the local name is {@code null}.
	 *
	 * @param element the DOM element
	 * @return the element name
	 */
	public static String getName(Element element) {
		String name = element.getLocalName();
		if (name != null) {
			return name;
		}
		return element.getTagName();
	}
	
	/**
	 * Counts the number of preceding sibling elements with the given tag name
	 * before the specified node.
	 *
	 * @param node    the reference node
	 * @param tagName the tag name to match
	 * @return the count of preceding matching siblings
	 */
	public static int countElementsBefore(Node node, String tagName) {
		
		Node parent = node.getParentNode();

		NodeList siblings = parent.getChildNodes();
		int count = 0;
		int siblingCount = siblings.getLength();

		for (int i = 0; i < siblingCount; ++i) {
			Node sibling = siblings.item(i);

			if (sibling == node) {
				break;
			}
			if ((sibling.getNodeType() == 1) && (((Element) sibling).getTagName().equals(tagName))) {
				++count;
			}
		}

		return count;
	}
	
	
	/**
	 * Counts the direct child nodes of the given element that match the
	 * specified DOM node type.
	 *
	 * @param element  the parent node
	 * @param nodeType the DOM node type constant (e.g. {@link Node#ELEMENT_NODE})
	 * @return the count of matching child nodes
	 */
	 public static int countChildElementsOfType(Node element, int nodeType) {
		NodeList children = element.getChildNodes();
		int count = 0;		
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeType() == nodeType) {
				count += 1;
			}
		}
		return count;
    }
	 
	/**
	 * Returns the first direct child node of the given element that matches
	 * the specified DOM node type.
	 *
	 * @param element  the parent node
	 * @param nodeType the DOM node type constant
	 * @return the first matching child node, or {@code null} if none found
	 */
	public static Node getFirstChildByType(Node element, int nodeType) {
		NodeList children = element.getChildNodes();
		int childCount = children.getLength();

		for (int i = 0; i < childCount; ++i) {
			Node child = children.item(i);
			if (child.getNodeType() == nodeType) {
				return child;
			}
		}

		return null;
	}
	
	/**
	 * Determines whether the given element represents a collection by
	 * checking for a {@code "Zero or more repetitions"} comment marker
	 * in its first child comment node.
	 *
	 * @param element the node to inspect
	 * @return {@code true} if the element is marked as a collection
	 */
	public static boolean assertIsCollection(Node element) {
		Comment firstComment = null;
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.COMMENT_NODE) {
				firstComment = (Comment) child;
				break;
			}
		}
		return ((firstComment != null) && (firstComment.getNodeValue().indexOf("Zero or more repetitions") != -1));
	}
	
}
