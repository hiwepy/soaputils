 package io.github.easy4j.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import io.github.easy4j.soap.SoapFaultUtils;


/**
 * Default {@link SoapResponseHandler} implementation that returns the raw
 * {@link SOAPMessage} after checking for SOAP faults. If a fault is
 * detected, an {@link InvokeException} is thrown.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see io.github.easy4j.soap.SoapFaultUtils
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

 
