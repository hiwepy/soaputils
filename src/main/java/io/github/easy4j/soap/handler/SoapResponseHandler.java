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
package io.github.easy4j.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Strategy interface for processing SOAP responses. Implementations
 * transform a raw {@link SOAPMessage} into a domain-specific type {@code T}
 * and may perform pre-processing on the underlying HTTP connection.
 *
 * @param <T> the response type produced by this handler
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see io.github.easy4j.soap.SoapUtils2
 */
public interface SoapResponseHandler<T> {

	/**
	 * Pre-processes the HTTP connection before the SOAP request is sent.
	 * Implementations may set custom headers, timeouts, or other connection
	 * properties.
	 *
	 * @param httpConn the HTTP connection to pre-process
	 */
	void preHandle(HttpURLConnection httpConn);

	/**
	 * Processes the SOAP response message and converts it to the target type.
	 *
	 * @param response the SOAP response message to process
	 * @return the converted response object, may be {@code null}
	 * @throws SOAPException if the response cannot be processed
	 */
    T handleResponse(SOAPMessage response) throws SOAPException;
    
}
