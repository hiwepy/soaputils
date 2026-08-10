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
package io.github.easy4j.soap.signature;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

/**
 * Strategy interface for adding security signatures or credentials to
 * SOAP request messages. Implementations can sign the SOAP header,
 * body, or both before the message is sent.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see DefaultSoapSignature
 * @see io.github.easy4j.soap.SoapRequestUtils
 */
public interface SoapSignature {

	/**
	 * Adds signature or authentication elements to the SOAP header.
	 *
	 * @param header    the SOAP header to sign
	 * @param namespace the target namespace for the signature elements
	 * @throws SOAPException if signing fails
	 */
	void sign(SOAPHeader header, String namespace) throws SOAPException;

	/**
	 * Adds signature or authentication elements to the SOAP body.
	 *
	 * @param body      the SOAP body to sign
	 * @param namespace the target namespace for the signature elements
	 * @throws SOAPException if signing fails
	 */
	void sign(SOAPBody body, String namespace) throws SOAPException;

}
