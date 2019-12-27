package com.github.hiwepy.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Soap请求响应处理：返回java.lang.String对象
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class PlainTextResponseHandler implements SoapResponseHandler<String> {

	@Override
	public void preHandle(HttpURLConnection httpConn) {
		
	} 

	@Override
	public String handleResponse(SOAPMessage response) throws SOAPException {
		
		
		return null;
	}

}
