package io.github.easy4j.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.alibaba.fastjson.JSONObject;

/**
 * {@link SoapResponseHandler} implementation that converts a SOAP response
 * into a {@link JSONObject}. This is a stub implementation that currently
 * returns {@code null} pending full JSON conversion support.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see SoapResponseHandler
 */
public class JSONResponseHandler implements SoapResponseHandler<JSONObject> {

	@Override
	public void preHandle(HttpURLConnection httpConn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject handleResponse(SOAPMessage response) throws SOAPException {
//		/return JSONObject.parseObject(result);
		// TODO Auto-generated method stub
		return null;
	}

	 
	
}
