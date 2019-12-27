/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package com.github.hiwepy.soap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.hiwepy.soap.type.SoapType;
import com.github.hiwepy.soap.type.SoapTypes;

public class SoapResponseUtils {

	public static void build(Map<String, Object> variables, JSONArray jarray, SOAPMessage message) throws Exception {

		SoapFaultUtils.checkFault(message);

		NodeList nodeList = message.getSOAPBody().getChildNodes();

		if ((nodeList == null) || (nodeList.getLength() < 1)) {
			return;
		}

		SOAPElement[] elements = new SOAPElement[nodeList.getLength()];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = ((SOAPElement) nodeList.item(i));
		}

		for (Iterator<Object> i$ = jarray.iterator(); i$.hasNext();) {
			Object obj = i$.next();
			JSONObject jobject = (JSONObject) obj;
			build(variables, elements, jobject);
		}
	}

	private static SOAPElement getElementByPath(SOAPElement[] elements, String fullpath) {
		if (StringUtils.isBlank(fullpath)) {
			return elements[0];
		}
		String[] names = fullpath.split("\\.");
		int size = names.length;

		SOAPElement root = null;
		Node node = elements[0].getFirstChild();
		if (node != null) {
			root = (SOAPElement) node;
		}

		if (root == null) {
			return null;
		}

		for (int i = 1; i < size; i++) {
			String name = names[i];
			root = getElement(root.getChildElements(), name);
		}
		return root;
	}

	private static SOAPElement getElement(Iterator<SOAPElement> it, String name) {
		while (it.hasNext()) {
			SOAPElement element = (SOAPElement) it.next();
			String tagName = element.getTagName();
			if (tagName.equals(name)) {
				return element;
			}
		}
		return null;
	}

	private static void build(Map<String, Object> variables, SOAPElement[] roots, JSONObject jobject) throws Exception {
		if (jobject == null)
			return;

		String binding = jobject.getString("bindingVal");
		String soapType = jobject.getString("soapType");
		String beanType = jobject.getString("javaType");
		Integer bindingType = Integer.valueOf(jobject.getIntValue("bindingType"));
		String fullpath = "";
		if (jobject.containsKey("fullpath")) {
			fullpath = jobject.getString("fullpath");
		}
		SOAPElement elements = getElementByPath(roots, fullpath);
		binding = StringEscapeUtils.unescapeJson(binding);

		if (StringUtils.isBlank(binding))
			return;

		Class<?> type = Class.forName(soapType);
		ConvertUtils.lookup(SOAPElement.class, type).convert(type, elements);

		Object obj = null;
		SoapType converter;
		if (StringUtils.isNotBlank(soapType)) {
			try {
				Class<?> kclass;
				if (soapType.matches("List\\{\\w*\\}")) {
					kclass = List.class;
				} else {
					kclass = Class.forName(soapType);
				}
				converter = SoapTypes.getTypeBySoap(soapType);
				obj = converter.convertToBean(kclass, new SOAPElement[] { elements });
			} catch (Exception ex) {
				converter = SoapTypes.getTypeBySoap("string");
				obj = converter.convertToBean(new SOAPElement[] { elements });
			}

		} else if ((StringUtils.isNotBlank(beanType)) && (bindingType.intValue() == 2)) {
			Class klass = Class.forName(beanType);
			converter = SoapTypes.getTypeByBean(klass);
			obj = converter.convertToBean(klass, new SOAPElement[] { elements });
		} else {
			obj = elements.getTextContent();
		}

		switch (bindingType.intValue()) {
		case 2:
			if (obj != null) {
				if ((!(obj instanceof List)) && (binding.indexOf("[i]") > -1)) {
					List list = new ArrayList();
					list.add(obj);
					obj = list;
				}

				PropertyUtils.setProperty(variables, binding, obj);
			}
			break;
		case 3:
			/*
			 * variables.put("returnObj", obj); SoapUtils.groovyEngine.evaluate(new
			 * StaticScriptSource(binding), variables); variables.remove("returnObj");
			 */
		}
	}

}
