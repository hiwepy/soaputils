package io.github.easy4j.soap.type;

import java.math.BigDecimal;
import javax.xml.soap.SOAPElement;

/**
 * {@link SoapType} implementation that handles numeric values including
 * {@link Integer}, {@link Long}, {@link Short}, {@link Double},
 * {@link Float} and {@link BigDecimal}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BaseSoapType
 * @see SoapTypes#number
 */
public class NumberSoapType extends BaseSoapType {
	public Class<?>[] getBeanTypes() {
		return new Class[] { Integer.TYPE, Integer.class, Long.TYPE, Long.class, Short.TYPE, Short.class, Double.TYPE,
				Double.class, Float.TYPE, Float.class, BigDecimal.class };
	}

	public String[] getSoapTypes() {
		return new String[] { "int", "long", "short", "integer" };
	}

	void setCurrentValue(SOAPElement element, Object obj, Class<?> klass) {
		element.setTextContent(obj.toString());
	}

	Object convertCurrent(Class<?> klass, SOAPElement element) {
		String value = element.getTextContent();
		if (klass == Integer.class)
			return Integer.valueOf(value);
		if (klass == Long.class)
			return Long.valueOf(value);
		if (klass == Short.class)
			return Short.valueOf(value);
		if (klass == Double.class)
			return Double.valueOf(value);
		if (klass == Float.class)
			return Float.valueOf(value);
		if (klass == BigDecimal.class) {
			return BigDecimal.valueOf(Double.valueOf(value).doubleValue());
		}
		return value;
	}
}
