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
package io.github.easy4j.soap;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import io.github.easy4j.soap.exception.InvokeException;

/**
 * Utility class for inspecting SOAP response messages and detecting faults.
 * When a SOAP fault is present in the response body, an {@link InvokeException}
 * is thrown containing the fault code and fault string from the response.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see io.github.easy4j.soap.exception.InvokeException
 * @see javax.xml.soap.SOAPFault
 */
public class SoapFaultUtils {

	/**
	 * Checks whether the given SOAP response contains a fault element.
	 * If a fault with a non-null fault code is found, an {@link InvokeException}
	 * is thrown with the fault code and fault string.
	 *
	 * @param response the SOAP response message to inspect
	 * @throws SOAPException  if the SOAP envelope cannot be read
	 * @throws InvokeException if the response contains a SOAP fault
	 */
	public static void checkFault(SOAPMessage response) throws SOAPException {
		SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
		SOAPBody body = envelope.getBody();
		SOAPFault fault = body.getFault();
		if ((fault != null) && (fault.getFaultCode() != null)) {
			throw new InvokeException(fault.getFaultCode(), fault.getFaultString());
		}
	}
	
}
