package org.apache.cxf.spring.boot.jaxws.soap.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ListSoapType extends BaseSoapType {
	private static Logger logger = LoggerFactory.getLogger(BaseSoapType.class);
	private Class<?> currentClass;

	public Class<?>[] getBeanTypes() {
		return new Class[] { List.class };
	}

	public String[] getSoapTypes() {
		return new String[] { "List" };
	}

	void setCurrentValue(SOAPElement element, Object obj, Class<?> klass) {
		String tagName = element.getTagName();
		try {
			List list = (List) obj;
			if (list == null)
				return;
			String elementName = element.getLocalName();
			SOAPElement parentElement = element.getParentElement();
			NodeList fieldNodeList = parentElement.getElementsByTagName(elementName);
			if (fieldNodeList == null)
				return;
			int nodeCount = fieldNodeList.getLength();
			if (nodeCount == list.size()) {
				for (int i = 0; i < nodeCount; i++) {
					Object item = list.get(i);
					this.currentClass = item.getClass();
					SOAPElement itemElement = (SOAPElement) fieldNodeList.item(i);
					SoapTypes.getTypeByBean(this.currentClass).setValue(itemElement, item, this.currentClass);
				}
			} else {
				Node tempElement = element.cloneNode(true);
				element.detachNode();
				Iterator i$;
				for (i$ = list.iterator(); i$.hasNext();) {
					Object item = i$.next();
					this.currentClass = item.getClass();
					SOAPElement itemElement = (SOAPElement) tempElement.cloneNode(true);
					parentElement.addChildElement(itemElement);
					SoapTypes.getTypeByBean(this.currentClass).setValue(itemElement, item, this.currentClass);
				}
			}
		} catch (Exception ex) {
			SOAPElement parentElement;
			Node tempElement;
			logger.warn("字段[" + tagName + "]设置失败.", ex);
		}
	}

	Object convertCurrent(Class<?> klass, SOAPElement element) {
		String tagName = element.getTagName();
		try {
			SOAPElement parentElement = element.getParentElement();
			NodeList nodeList = parentElement.getElementsByTagName(tagName);
			int size = nodeList.getLength();
			List list = new ArrayList();
			for (int i = 0; i < size; i++) {
				SOAPElement node = (SOAPElement) nodeList.item(i);
				String text = node.getTextContent();
				if (!StringUtils.hasText(text)) {
					SoapType convert = SoapTypes.getTypeByBean(null);
					Class c = Object.class;
					try {
						c = Class.forName(node.getTagName());
					} catch (Exception e) {
					}
					Object obj = convert.convertToBean(c, new SOAPElement[] { element });
					list.add(obj);
				} else {
					list.add(text);
				}
			}
			return list;
		} catch (Exception ex) {
			logger.warn("字段[" + tagName + "]设置失败.", ex);
		}
		return null;
	}
}