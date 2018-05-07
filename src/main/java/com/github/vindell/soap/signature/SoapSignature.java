/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package com.github.vindell.soap.signature;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

public interface SoapSignature {

	/**
	 * 通过Header增加签名参数
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param header
	 */
	void sign(SOAPHeader header, String namespace) throws SOAPException;
	
	/**
	 * 通过Body增加签名参数
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param body
	 */
	void sign(SOAPBody body, String namespace) throws SOAPException;
	
}
