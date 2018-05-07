/*
 * Copyright (c) 2018, vindell (hnxyhcwdl1003@163.com).
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
package com.github.vindell.soap;

import java.net.HttpURLConnection;

import junit.framework.TestCase;

public class HttpStatusTest extends TestCase {
	 
	public void testStatus() {
		
		
		/* 2XX: generally "OK" */
		
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_OK));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_CREATED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_ACCEPTED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_NOT_AUTHORITATIVE));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_NO_CONTENT));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_RESET));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_PARTIAL));
        
        /* 3XX: relocation/redirect */

        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_MULT_CHOICE));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_MOVED_PERM));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_MOVED_TEMP));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_SEE_OTHER));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_NOT_MODIFIED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_USE_PROXY));
        
        /* 4XX: client error */
        
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_BAD_REQUEST));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_UNAUTHORIZED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_PAYMENT_REQUIRED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_FORBIDDEN));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_NOT_FOUND));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_BAD_METHOD));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_NOT_ACCEPTABLE));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_PROXY_AUTH));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_CLIENT_TIMEOUT));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_CONFLICT));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_GONE));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_LENGTH_REQUIRED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_PRECON_FAILED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_ENTITY_TOO_LARGE));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_REQ_TOO_LONG));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_UNSUPPORTED_TYPE));
        
        /* 5XX: server error */
        
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_INTERNAL_ERROR));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_NOT_IMPLEMENTED));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_BAD_GATEWAY));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_UNAVAILABLE));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_GATEWAY_TIMEOUT));
        System.out.println(HttpStatus.getStatusText(HttpURLConnection.HTTP_VERSION));
        
	}
	
}
