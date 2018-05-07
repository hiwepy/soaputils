package org.apache.cxf.spring.boot.jaxws.soap.type;

import java.util.ArrayList;
import java.util.List;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

abstract class BaseSoapType implements SoapType {
	
	public abstract Class<?>[] getBeanTypes();

	public abstract String[] getSoapTypes();

	private final Class<?> getDefaultClass() {
		Class[] klasses = getBeanTypes();
		if ((klasses == null) || (klasses.length == 0)) {
			return Object.class;
		}

		return klasses[0];
	}

	abstract Object convertCurrent(Class<?> paramClass, SOAPElement paramSOAPElement);

	public final Object convertToBean(Class<?> klass, SOAPElement[] elements) throws SOAPException {
		if ((elements == null) || (elements.length < 1)) {
			return null;
		}

		if (elements.length > 1) {
			List list = new ArrayList();
			for (SOAPElement element : elements) {
				Object obj = convertCurrent(klass, element);
				list.add(obj);
			}
			return list;
		}
		return convertCurrent(klass, elements[0]);
	}

	abstract void setCurrentValue(SOAPElement paramSOAPElement, Object paramObject, Class<?> paramClass);

	public final void setValue(SOAPElement element, Object obj, Class<?> klass) throws SOAPException {
		if (obj == null) {
			return;
		}
		if (klass == null)
			klass = getDefaultClass();
		setCurrentValue(element, obj, klass);
	}

	public final Object convertToBean(SOAPElement[] elements) throws SOAPException {
		return convertToBean(getDefaultClass(), elements);
	}

	public final void setValue(SOAPElement element, Object obj) throws SOAPException {
		setValue(element, obj, getDefaultClass());
	}
}
