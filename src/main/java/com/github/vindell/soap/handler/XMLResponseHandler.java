 package com.github.vindell.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

/**
 * Soap请求响应处理：返回org.w3c.dom.Document对象
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class XMLResponseHandler implements SoapResponseHandler<Document> {

	@Override
	public void preHandle(HttpURLConnection httpConn) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Document handleResponse(SOAPMessage response) throws SOAPException {
		// 响应消息处理,将响应的消息转换为doc对象  
		Document doc = response.getSOAPPart().getEnvelope().getBody()  
		        .extractContentAsDocument();  
		
		return doc;
	}

	
	
}

 
