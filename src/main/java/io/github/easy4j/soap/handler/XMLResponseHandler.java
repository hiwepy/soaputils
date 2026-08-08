 package io.github.easy4j.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

/**
 * {@link SoapResponseHandler} implementation that extracts the SOAP body
 * content as a {@link org.w3c.dom.Document} via
 * {@link javax.xml.soap.SOAPBody#extractContentAsDocument()}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see SoapResponseHandler
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

 
