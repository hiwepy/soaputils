package io.github.easy4j.soap.type;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * Interface defining the contract for converting between Java objects and
 * SOAP element content. Each implementation handles a specific category
 * of types (string, number, date, bean, list, etc.).
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see SoapTypes
 * @see BaseSoapType
 */
public abstract interface SoapType {
	
	/**
	 * Converts one or more SOAP elements into a Java object of the specified type.
	 * @param paramClass             the target Java class
	 * @param paramArrayOfSOAPElement the source SOAP elements
	 * @return the converted Java object (or a list if multiple elements are provided)
	 * @throws SOAPException if conversion fails
	 */
	public abstract Object convertToBean(Class<?> paramClass, SOAPElement[] paramArrayOfSOAPElement)
			throws SOAPException;

	/**
	 * Converts one or more SOAP elements into a Java object using the default type.
	 * @param paramArrayOfSOAPElement the source SOAP elements
	 * @return the converted Java object
	 * @throws SOAPException if conversion fails
	 */
	public abstract Object convertToBean(SOAPElement[] paramArrayOfSOAPElement) throws SOAPException;

	/**
	 * Sets the given Java object value on the specified SOAP element using the
	 * provided class for type resolution.
	 * @param paramSOAPElement the target SOAP element
	 * @param paramObject      the Java value to set
	 * @param paramClass       the class used for type-specific conversion
	 * @throws SOAPException if setting the value fails
	 */
	public abstract void setValue(SOAPElement paramSOAPElement, Object paramObject, Class<?> paramClass)
			throws SOAPException;

	/**
	 * Sets the given Java object value on the specified SOAP element using the default type.
	 * @param paramSOAPElement the target SOAP element
	 * @param paramObject      the Java value to set
	 * @throws SOAPException if setting the value fails
	 */
	public abstract void setValue(SOAPElement paramSOAPElement, Object paramObject) throws SOAPException;

	/**
	 * Returns the Java types this converter handles.
	 * @return array of supported Java classes
	 */
	public abstract Class<?>[] getBeanTypes();

	/**
	 * Returns the SOAP type names this converter handles.
	 * @return array of supported SOAP type name strings
	 */
	public abstract String[] getSoapTypes();
	
}
