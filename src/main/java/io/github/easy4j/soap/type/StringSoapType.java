package io.github.easy4j.soap.type;

import javax.xml.soap.SOAPElement;

/**
 * {@link SoapType} implementation that handles {@link String} values.
 * Converts between Java strings and SOAP element text content.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BaseSoapType
 * @see SoapTypes#string
 */
public class StringSoapType extends BaseSoapType {
	
	public Class<?>[] getBeanTypes() {
		return new Class[] { String.class };
	}

	public String[] getSoapTypes() {
		return new String[] { "string" };
	}

	void setCurrentValue(SOAPElement element, Object obj, Class<?> klass) {
		element.setTextContent(obj.toString());
	}

	Object convertCurrent(Class<?> klass, SOAPElement element) {
		return element.getTextContent();
	}
	
}
