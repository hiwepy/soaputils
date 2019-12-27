/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package com.github.hiwepy.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Handler that encapsulates the process of generating a response object
 * from a {@link javax.xml.soap.SOAPMessage}.
 */
public interface SoapResponseHandler<T> {

	/**
	 * 对HttpURLConnection进行预处理
	 * @param httpConn {@link java.net.HttpURLConnection} 对象
	 */
	void preHandle(HttpURLConnection httpConn);
	
	/**
	 * TODO
	 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
	 * @param response
	 * @return
	 * @throws SOAPException
	 */
    T handleResponse(SOAPMessage response) throws SOAPException;
    
}
