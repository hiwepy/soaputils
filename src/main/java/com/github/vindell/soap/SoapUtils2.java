package com.github.vindell.soap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Node;

import com.github.vindell.soap.handler.DefaultResponseHandler;
import com.github.vindell.soap.handler.SoapResponseHandler;
import com.github.vindell.soap.signature.DefaultSoapSignature;
import com.github.vindell.soap.signature.SoapSignature;

public class SoapUtils2 {
	
	private static Integer _connTimeout = Integer.valueOf(0);

	private static Integer _readTimeout = Integer.valueOf(0);
	
	public static SoapResponseHandler<SOAPMessage> DEFAULT_HANDLER = new DefaultResponseHandler();
	public static SoapSignature DEFAULT_SIGNATURE = new DefaultSoapSignature();
	
	public static String getAttribute(Node node, String name) {
		Node tmp = node.getAttributes().getNamedItem(name);
		return tmp != null ? tmp.getTextContent() : null;
	}
	
	public static SOAPMessage soapRequest(String namespace, String wsdlUrl, String method, Map<String, Object> params) throws SOAPException {
		return soapRequest(namespace, wsdlUrl, method, params, DEFAULT_HANDLER, DEFAULT_SIGNATURE );
	}
	
	public static <T> T soapRequest(String namespace, String wsdlUrl, String method, Map<String, Object> params, 
			SoapResponseHandler<T> handler) throws SOAPException {
		return soapRequest(namespace, wsdlUrl, method, params, handler, DEFAULT_SIGNATURE);
	}
	
	public static <T> T soapRequest(String namespace, String wsdlUrl, String method, Map<String, Object> params,
			SoapResponseHandler<T> handler, SoapSignature signature) throws SOAPException {
		
		notNull(wsdlUrl, "The wsdlUrl must not be null");
		notNull(namespace, "The namespace must not be null");
		notNull(method, "The method must not be null");
		
		SOAPMessage soapRequest = SoapRequestUtils.buildRequest(namespace, wsdlUrl, method, null, params, signature);
		// 执行请求
		return invoke(wsdlUrl, soapRequest, handler);
	}
	
	public static SOAPMessage soap12Request(String namespace, String wsdlUrl, String method, Map<String, Object> params) throws SOAPException {
		return soap12Request(namespace, wsdlUrl, method, params, DEFAULT_HANDLER, DEFAULT_SIGNATURE );
	}
	
	public static <T> T soap12Request(String namespace, String wsdlUrl, String method, Map<String, Object> params, 
			SoapResponseHandler<T> handler) throws SOAPException {
		return soap12Request(namespace, wsdlUrl, method, params, handler, DEFAULT_SIGNATURE);
	}
	
	public static <T> T soap12Request(String namespace, String wsdlUrl, String method, Map<String, Object> params,
			SoapResponseHandler<T> handler, SoapSignature signature) throws SOAPException {
		
		notNull(wsdlUrl, "The wsdlUrl must not be null");
		notNull(namespace, "The namespace must not be null");
		notNull(method, "The method must not be null");
		
		SOAPMessage soapRequest = SoapRequestUtils.buildRequest(namespace, wsdlUrl, method, SOAPConstants.SOAP_1_2_PROTOCOL, params, signature);
		// 执行请求
		return invoke(wsdlUrl, soapRequest, handler);
	}
	
	private static <T> T invoke(String wsdlUrl, SOAPMessage request, final SoapResponseHandler<T> handler) throws SOAPException {
		// 实例化一个soap连接对象工厂
		SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
		// 实例化一个连接对象
		SOAPConnection connection = null;
		try {
			URL invokeURL = new URL(wsdlUrl);
			URL toPoint = new URL(null, invokeURL.toString(), new URLStreamHandler() {
				
				protected URLConnection openConnection(URL u) throws IOException {
					
					URL clone_url = new URL(u.toString());
					
					HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();
 
					clone_urlconnection.setConnectTimeout(SoapUtils2._connTimeout.intValue());
					clone_urlconnection.setReadTimeout(SoapUtils2._readTimeout.intValue());
					
					handler.preHandle(clone_urlconnection);
					
					return clone_urlconnection;
				}
			});
			// 创建请求连接
			connection = soapConnFactory.createConnection();
			// 下面为调用
			SOAPMessage reply = connection.call(request, toPoint);
		    // 如果返回的消息不为空则进行处理
		    if( null != reply) {
		    	return handler.handleResponse(reply);
		    }
		    return null;
		} catch (Exception ex) {
			throw new SOAPException(ex);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	/**
	 * Assert that an object is not {@code null}.
	 * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
}
