 package com.github.vindell.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.github.vindell.soap.SoapFaultUtils;


/**
 * Soap请求响应处理：返回 javax.xml.soap.SOAPMessage 对象
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class DefaultResponseHandler implements SoapResponseHandler<SOAPMessage> {

	@Override
	public void preHandle(HttpURLConnection httpConn) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public SOAPMessage handleResponse(SOAPMessage response) throws SOAPException {
		// 第一步检查结果
		SoapFaultUtils.checkFault(response);
		
		return response;
	}
	
}

 
