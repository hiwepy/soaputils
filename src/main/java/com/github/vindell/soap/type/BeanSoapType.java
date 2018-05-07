package org.apache.cxf.spring.boot.jaxws.soap.type;

import java.lang.reflect.Field;
import java.util.Iterator;

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.NodeList;

public class BeanSoapType extends BaseSoapType {
	private static Logger logger = LoggerFactory.getLogger(BaseSoapType.class);

	public Class<?>[] getBeanTypes() {
		return new Class[] { Object.class };
	}

	public String[] getSoapTypes() {
		return new String[] { "anyType" };
	}

	void setCurrentValue(SOAPElement element, Object obj, Class<?> klass) {
		Class<? extends Object> myKlass = obj.getClass();
		if (myKlass != null) {
			klass = myKlass;
		}
		for (Field field : klass.getDeclaredFields()) {
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
			String fieldName = field.getName();
			fieldName = fieldName.replace("$cglib_prop_", "");
			NodeList fieldNodeList = element.getElementsByTagName(fieldName);
			if ((fieldNodeList != null) && (fieldNodeList.getLength() >= 1)) {
				try {
					Object objValue = field.get(obj);
					SOAPElement targetElement = (SOAPElement) fieldNodeList.item(0);
					if (ObjectUtils.isEmpty(objValue)) {
						boolean hasChild = targetElement.hasChildNodes();
						if (!hasChild) {
							targetElement.detachNode();
						}
					} else {
						SoapTypes.getTypeByBean(fieldType).setValue(targetElement, objValue, fieldType);
					}
				} catch (Exception e) {
					logger.warn("字段[" + fieldName + "]设置失败.", e);
				}
			}
		}
		Iterator<?> it = element.getChildElements();
		while (it.hasNext()) {
			SOAPElement child = (SOAPElement) it.next();
			if (!child.hasChildNodes()) {
				String content = child.getTextContent();
				if (!StringUtils.hasText(content)) {
					child.detachNode();
				}
			}
		}
	}

	Object convertCurrent(Class<?> klass, SOAPElement element) {
		Object bean;
		try {
			bean = klass.newInstance();
		} catch (Exception e) {
			logger.error("类别[" + klass + "]无法实例化.", e);
			return null;
		}

		for (Field field : klass.getDeclaredFields()) {
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
			String fieldName = field.getName();
			NodeList fieldNodeList = element.getElementsByTagName(fieldName);
			if ((fieldNodeList != null) && (fieldNodeList.getLength() >= 1)) {
				try {
					Object obj = SoapTypes.getTypeByBean(fieldType).convertToBean(fieldType,
							new SOAPElement[] { element });
					field.set(bean, obj);
				} catch (Exception e) {
					logger.warn("字段[" + fieldName + "]设置失败.", e);
				}
			}
		}

		return bean;
	}
}
