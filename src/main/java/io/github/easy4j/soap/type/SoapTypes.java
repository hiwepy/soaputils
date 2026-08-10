package io.github.easy4j.soap.type;

import java.util.Arrays;
import java.util.List;

/**
 * Registry of all built-in {@link SoapType} converters. Each enum constant
 * pairs a descriptive name with its {@link SoapType} implementation. Provides
 * lookup methods to resolve the correct converter by Java class or SOAP type name.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see SoapType
 */
public enum SoapTypes {
	string("字符串(string,java.lang.String)", new StringSoapType()),

	number("数字(number,java.lang.Integer,java.lang.Long,java.math.BigDecimal)", new NumberSoapType()),

	date("时间/日期(date,java.util.Date,dateTime)", new DateSoapType()),

	bean("复合类型(bean,serializable)", new BeanSoapType()),

	list("列表类型(List)", new ListSoapType());

	private SoapType soapType;
	private String name;

	private SoapTypes(String name, SoapType soapType) {
		this.name = name;
		this.soapType = soapType;
	}

	/**
	 * Returns the {@link SoapType} converter for this enum constant.
	 * @return the associated soap type converter
	 */
	public SoapType getSoapType() {
		return this.soapType;
	}

	/**
	 * Returns the human-readable description of this type constant.
	 * @return the descriptive name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return name();
	}

	/**
	 * Finds the {@link SoapType} converter that handles the given Java class.
	 * Returns the {@link #bean} converter as a fallback.
	 *
	 * @param klass the Java class to look up (may be {@code null})
	 * @return the matching {@link SoapType}, never {@code null}
	 */
	public static SoapType getTypeByBean(Class<?> klass) {
		if (klass == null) {
			return bean.getSoapType();
		}

		for (SoapTypes types : values()) {
			if (Arrays.asList(types.getSoapType().getBeanTypes()).contains(klass)) {
				return types.getSoapType();
			}
		}

		return bean.getSoapType();
	}

	/**
	 * Finds the {@link SoapType} converter that handles the given SOAP type
	 * name string. Returns the {@link #bean} converter as a fallback.
	 *
	 * @param type the SOAP type name (e.g. {@code "string"}, {@code "int"}), may be {@code null}
	 * @return the matching {@link SoapType}, never {@code null}
	 */
	public static SoapType getTypeBySoap(String type) {
		if (type == null) {
			return bean.getSoapType();
		}
		if (type.matches("List\\{\\w*\\}")) {
			return new ListSoapType();
		}
		for (SoapTypes types : values()) {
			if (Arrays.asList(types.getSoapType().getSoapTypes()).contains(type)) {
				return types.getSoapType();
			}
		}
		return bean.getSoapType();
	}
}
