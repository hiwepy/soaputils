package io.github.easy4j.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * {@link SoapResponseHandler} implementation that converts a SOAP response
 * into a plain-text {@link String}. This is a stub implementation that
 * currently returns {@code null} pending full text extraction support.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see SoapResponseHandler
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
