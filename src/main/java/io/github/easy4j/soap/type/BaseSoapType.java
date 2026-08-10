package io.github.easy4j.soap.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * Abstract base implementation of {@link SoapType} that provides the
 * template-method pattern for type conversion. Subclasses implement
 * {@link #convertCurrent(Class, SOAPElement)} and
 * {@link #setCurrentValue(SOAPElement, Object, Class)} for their
 * specific type handling logic.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see SoapType
 * @see SoapTypes
 */
abstract class BaseSoapType implements SoapType {
	
	/**
	 * Returns the Java bean types supported by this converter.
	 * @return array of supported classes
	 */
	public abstract Class<?>[] getBeanTypes();

	/**
	 * Returns the SOAP type names supported by this converter.
	 * @return array of SOAP type name strings
	 */
	public abstract String[] getSoapTypes();

	/**
	 * Returns the first element from {@link #getBeanTypes()}, or
	 * {@code Object.class} if no types are declared.
	 * @return the default Java class for this type
	 */
	private final Class<?> getDefaultClass() {
		Class[] klasses = getBeanTypes();
		if ((klasses == null) || (klasses.length == 0)) {
			return Object.class;
		}

		return klasses[0];
	}

	/**
	 * Template method that converts a single SOAP element into a Java object.
	 * @param paramClass     the target Java class
	 * @param paramSOAPElement the source SOAP element
	 * @return the converted Java object
	 */
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

	/**
	 * Template method that sets the value of a Java object onto a SOAP element.
	 * @param paramSOAPElement the target SOAP element
	 * @param paramObject      the Java value to set
	 * @param paramClass       the class for type-specific conversion
	 */
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
