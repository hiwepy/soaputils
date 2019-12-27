package com.github.hiwepy.soap.handler;

import java.net.HttpURLConnection;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.alibaba.fastjson.JSONObject;

/**
 * Soap请求响应处理：返回JSONObject对象
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
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
