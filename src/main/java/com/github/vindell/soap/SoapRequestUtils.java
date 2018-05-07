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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.github.vindell.soap.signature.SoapSignature;
import com.github.vindell.soap.type.SoapType;
import com.github.vindell.soap.type.SoapTypes;

public class SoapRequestUtils {

	private static Logger logger = LoggerFactory.getLogger(SoapRequestUtils.class);
	
	
	public static SOAPMessage buildRequest(String namespace, String wsdlUrl, String method, String protocol,
			Map<String, Object> params, SoapSignature signature) throws SOAPException {
		return buildRequest(createRequest(null, null, namespace, method, params),
				namespace, protocol, null);
	}
	
	private static SOAPMessage buildRequest(SOAPElement element, String namespace, String protocol, SoapSignature signature) throws SOAPException {
		
		// 实例化一个消息对象 
		MessageFactory messageFactory = null != protocol ? MessageFactory.newInstance(protocol) : MessageFactory.newInstance();
		// 实例化一个消息  
		SOAPMessage message = messageFactory.createMessage();
		
		message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
		
		if (StringUtils.isNotEmpty(namespace)) {
			MimeHeaders mHers = message.getMimeHeaders();
			mHers.setHeader("SOAPAction", element.getElementQName().getNamespaceURI() + element.getElementQName().getLocalPart());
		}
		// 获取消息中soap消息部分的句柄 
		SOAPPart soapPart = message.getSOAPPart();
		// 获取soap消息部分中的信封句柄
		SOAPEnvelope envelope = soapPart.getEnvelope();
		// 创建SOAPHeader(不是必需)  
		SOAPHeader header = envelope.getHeader();  
        if (header == null) {
        	header = envelope.addHeader();
        }
        signature.sign(header, namespace);
        
        // 获取信封中需要填写的请求报文部分的句柄
		SOAPBody body = envelope.getBody();
		
		signature.sign(body, namespace);
		
		body.addChildElement(element);
		
		//保存消息的修改               
	    message.saveChanges();
	    
		return message;
	}

	private static void buildSoapElementValue(SOAPElement soapElement, JSONObject jobject, Map<String, Object> variables)
			throws Exception {
		if (jobject == null)
			return;

		String binding = jobject.getString("bindingVal");
		String soapType = jobject.getString("soapType");
		String javaType = jobject.getString("javaType");
		Integer bindingType = Integer.valueOf(jobject.getIntValue("bindingType"));
		binding = StringEscapeUtils.unescapeJson(binding);

		String listObj = "";
		String elementStr = "";
		Pattern regex = Pattern.compile("^.*\\.(\\w+)\\[i\\](\\.\\w+)?$");
		Matcher regexMatcher = regex.matcher(binding);
		if (regexMatcher.find()) {
			listObj = regexMatcher.group(1);
			elementStr = regexMatcher.group(2);
		}
		try {
			Object obj = null;
			switch (bindingType.intValue()) {
			case 1:
				soapElement.setTextContent(binding);
				break;
			case 2:
				if (StringUtils.hasText(binding))
					obj = PropertyUtils.getProperty(variables, binding);
				break;
			case 3:
				obj = SoapUtils.groovyEngine.evaluate(new StaticScriptSource(binding), variables);
			}

			if (obj != null) {
				SoapType converter = null;
				Class klass = null;

				if (soapType != null) {
					converter = SoapTypes.getTypeBySoap(soapType);
				} else if (javaType != null) {
					klass = Class.forName(javaType);
					converter = SoapTypes.getTypeByBean(klass);
				}

				if (StringUtils.hasText(listObj)) {
					if ((obj instanceof List)) {
						List list = (List) obj;
						String elementName = soapElement.getLocalName();
						SOAPElement parentElement = soapElement;

						if (StringUtils.hasText(elementStr)) {
							parentElement = soapElement.getParentElement();
						}
						if (list.size() == 0) {
							parentElement.detachNode();
							return;
						}

						SOAPElement grandpaElement = parentElement.getParentElement();
						listObj = parentElement.getTagName();
						NodeList fieldNodeList = grandpaElement.getElementsByTagName(listObj);
						if (fieldNodeList == null)
							return;
						int nodeCount = fieldNodeList.getLength();
						int listSize = list.size();
						int diffCount = listSize - nodeCount;

						for (int i = 0; i < diffCount; i++) {
							SOAPElement cloneElement = (SOAPElement) parentElement.cloneNode(true);
							grandpaElement.addChildElement(cloneElement);
						}
						fieldNodeList = grandpaElement.getElementsByTagName(listObj);
						for (int i = 0; i < listSize; i++) {
							Object item = list.get(i);
							SOAPElement listElement = (SOAPElement) fieldNodeList.item(i);
							SOAPElement itemElement = listElement;
							if (StringUtils.hasText(elementStr))
								itemElement = (SOAPElement) listElement.getElementsByTagName(elementName).item(0);
							if (item == null) {
								itemElement.detachNode();
							} else if (converter != null) {
								converter.setValue(itemElement, item, klass);
							} else {
								itemElement.setTextContent(item.toString());
							}
						}
					}

				} else if (converter != null) {
					converter.setValue(soapElement, obj, klass);
				} else {
					soapElement.setTextContent(obj.toString());
				}
			}

			String textContext = soapElement.getTextContent();
			boolean hasChild = soapElement.hasChildNodes();
			if ((!StringUtils.hasText(textContext)) && (!hasChild)) {
				soapElement.detachNode();
			}
		} catch (Exception e) {
			logger.error("动态设值出错.", e);
			throw e;
		}
	}

	private static SOAPElement createRequest(JSONArray jarray, JSONArray inputParams, String namespace,
			String method, Map<String, Object> variables) throws Exception {
		String prefix = "api";
		if (!StringUtils.hasText(namespace)) {
			prefix = "";
		}
		SOAPFactory factory = SOAPFactory.newInstance();
		SOAPElement bodyElement = factory.createElement(method, prefix, namespace);
		Map<JSONObject, SOAPElement> map;
		Iterator<?> it;
		Iterator<?> i$;
		if (!ObjectUtils.isEmpty(inputParams)) {
			
			map = new HashMap<JSONObject, SOAPElement>();
			for (i$ = inputParams.iterator(); i$.hasNext();) {
				Object obj = i$.next();
				JSONObject jobject = (JSONObject) obj;
				if (jobject != null) {
					String rootName = jobject.getString("name");
					// 增加Body元素和值  
					SOAPElement rootElement = bodyElement.addChildElement(rootName);

					setRequestStruct(jobject, rootElement, 1);

					setBindingValue(jarray, rootElement, 1, rootName, variables, map);
				}
			}
			for (it = map.keySet().iterator(); it.hasNext();) {
				JSONObject bindingJobject = (JSONObject) it.next();
				SOAPElement soapElement = (SOAPElement) map.get(bindingJobject);
				buildSoapElementValue(soapElement, bindingJobject, variables);
			}
		} else {
			for (i$ = jarray.iterator(); i$.hasNext();) {
				Object obj = i$.next();
				JSONObject jobject = (JSONObject) obj;
				if (jobject != null) {
					String paramName = jobject.getString("name");
					SOAPElement element = bodyElement.addChildElement(paramName);

					buildSoapElementValue(element, jobject, variables);
				}
			}
		}
		return bodyElement;
	}

	private static void setRequestStruct(JSONObject jobject, SOAPElement soapElement, int level)
			throws SOAPException {
		String paramName = jobject.getString("name");
		String type = jobject.getString("type");
		SOAPElement element = null;

		if (level == 1) {
			element = soapElement;
		} else
			element = soapElement.addChildElement(paramName);
		Iterator<?> i$;
		if (("bean".equals(type)) && (jobject.containsKey("children"))) {
			JSONArray children = jobject.getJSONArray("children");
			level++;
			for (i$ = children.iterator(); i$.hasNext();) {
				Object obj = i$.next();
				JSONObject childObject = (JSONObject) obj;
				if (childObject != null) {
					setRequestStruct(childObject, element, level);
				}
			}
		}
	}

	private static void setBindingValue(JSONArray jarray, SOAPElement soapElment, int level, String rootName,
			Map<String, Object> variables, Map<JSONObject, SOAPElement> map) throws Exception {
		String nodeName = soapElment.getNodeName();
		JSONObject bindingJobject = getBindingJObject(jarray, level, rootName, nodeName);
		Iterator<?> it = soapElment.getChildElements();
		level++;
		if (bindingJobject == null) {
			if (!it.hasNext()) {
				soapElment.detachNode();
			} else {
				while (it.hasNext()) {
					SOAPElement child = (SOAPElement) it.next();
					setBindingValue(jarray, child, level, rootName + "." + child.getNodeName(), variables, map);
				}
			}
		} else
			map.put(bindingJobject, soapElment);
	}

	private static JSONObject getBindingJObject(JSONArray jarray, int level, String rootName, String nodeName) {
		JSONObject reJobject = null;
		for (Iterator<?> i$ = jarray.iterator(); i$.hasNext();) {
			Object obj = i$.next();
			JSONObject jobject = (JSONObject) obj;
			if (jobject != null) {
				String paramName = jobject.getString("name");
				if (paramName.equals(nodeName)) {
					String fullpath = "";
					if (jobject.containsKey("fullpath")) {
						fullpath = jobject.getString("fullpath");
					}
					if (!StringUtils.hasText(fullpath)) {
						if (fullpath.equals(rootName))
							reJobject = jobject;
					} else {
						List<String> pathAry = Arrays.asList(jobject.getString("bindingVal").split("\\."));

						if (level == pathAry.size())
							reJobject = jobject;
					}
				}
			}
		}
		return reJobject;
	}
	
	
}
