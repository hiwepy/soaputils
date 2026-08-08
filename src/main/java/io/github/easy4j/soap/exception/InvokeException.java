/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
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
package io.github.easy4j.soap.exception;

import javax.xml.soap.SOAPException;

/**
 * Exception thrown when a SOAP service invocation returns a fault response.
 * Extends {@link javax.xml.soap.SOAPException} with a fault {@code code}
 * and {@code msg} extracted from the SOAP Fault element.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see io.github.easy4j.soap.SoapFaultUtils
 */
@SuppressWarnings("serial")
public class InvokeException extends SOAPException {
	
	private String code;
	private String msg;

	/**
	 * Constructs an exception with the given fault code and message.
	 *
	 * @param code the SOAP fault code
	 * @param msg  the SOAP fault string
	 */
	public InvokeException(String code, String msg) {
		this(code, msg, null);
	}

	/**
	 * Constructs an exception with the given fault code, message and root cause.
	 *
	 * @param code the SOAP fault code
	 * @param msg  the SOAP fault string
	 * @param e    the underlying cause
	 */
	public InvokeException(String code, String msg, Throwable e) {
		super(e);
		this.code = code;
		this.msg = msg;
	}

	/**
	 * Returns the SOAP fault code.
	 * @return the fault code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Returns the SOAP fault string.
	 * @return the fault message
	 */
	public String getMsg() {
		return this.msg;
	}
	
}
