/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package com.github.hiwepy.soap;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
//import javax.xml.soap.SOAPHeader;  
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;

import com.sun.xml.internal.ws.client.BindingProviderProperties;
import com.sun.xml.internal.ws.developer.JAXWSProperties;  

public class WebServiceClient {  
  String nameSpace = "";  
  String wsdlUrl = "";  
  String serviceName = "";  
  String portName = "";  
  String responseName = "";  
  String elementName = "";  
  int timeout = 20000;  

  /** 
   *  
   * @param nameSpace 
   * @param wsdlUrl 
   * @param serviceName 
   * @param portName 
   * @param elementName 
   * @param responseName 
   */  

  public WebServiceClient(String nameSpace, String wsdlUrl,  
          String serviceName, String portName, String element,  
          String responseName) {  
      this.nameSpace = nameSpace;  
      this.wsdlUrl = wsdlUrl;  
      this.serviceName = serviceName;  
      this.portName = portName;  
      this.elementName = element;  
      this.responseName = responseName;  
  }  

  /** 
   *  
   * @param nameSpace 
   * @param wsdlUrl 
   * @param serviceName 
   * @param portName 
   * @param elementName 
   * @param requestName 
   * @param responseName 
   * @param timeOut 
   *            毫秒 
   */  

  public WebServiceClient(String nameSpace, String wsdlUrl,  
          String serviceName, String portName, String element,  
          String responseName, int timeOut) {  
      this.nameSpace = nameSpace;  
      this.wsdlUrl = wsdlUrl;  
      this.serviceName = serviceName;  
      this.portName = portName;  
      this.elementName = element;  
      this.responseName = responseName;  
      this.timeout = timeOut;  
  }  

  public String sendMessage(HashMap<String, String> inMsg) throws Exception {  
      // 创建URL对象  
      URL url = null;  
      try {  
          url = new URL(wsdlUrl);  
      } catch (Exception e) {  
          e.printStackTrace();  
          return "创建URL对象异常";  
      }  
      // 创建服务(Service)  
      QName sname = new QName(nameSpace, serviceName);  
      Service service = Service.create(url, sname);  

      // 创建Dispatch对象  
      Dispatch<SOAPMessage> dispatch = null;  
      try {  
          dispatch = service.createDispatch(new QName(nameSpace, portName),  
                  SOAPMessage.class, Service.Mode.MESSAGE);  
      } catch (Exception e) {  
          e.printStackTrace();  
          return "创建Dispatch对象异常";  
      }  

      // 创建SOAPMessage  
      try {  
          SOAPMessage msg = MessageFactory.newInstance(  
                  SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();  
          msg.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");  

          SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();  

          // 创建SOAPHeader(不是必需)  
          // SOAPHeader header = envelope.getHeader();  
          // if (header == null)  
          // header = envelope.addHeader();  
          // QName hname = new QName(nameSpace, "username", "nn");  
          // header.addHeaderElement(hname).setValue("huoyangege");  

          // 创建SOAPBody  
          SOAPBody body = envelope.getBody();  
          QName ename = new QName(nameSpace, elementName, "q0");  
          SOAPBodyElement ele = body.addBodyElement(ename);  
          // 增加Body元素和值  
          for (Map.Entry<String, String> entry : inMsg.entrySet()) {  
              ele.addChildElement(new QName(nameSpace, entry.getKey()))  
                      .setValue(entry.getValue());  
          }  

          // 超时设置  
          dispatch.getRequestContext().put(  
                  BindingProviderProperties.CONNECT_TIMEOUT, timeout);  
          dispatch.getRequestContext().put(JAXWSProperties.REQUEST_TIMEOUT,  
                  timeout);  

          // 通过Dispatch传递消息,会返回响应消息  
          SOAPMessage response = dispatch.invoke(msg);  

          // 响应消息处理,将响应的消息转换为doc对象  
          Document doc = response.getSOAPPart().getEnvelope().getBody()  
                  .extractContentAsDocument();  
          String ret = doc.getElementsByTagName(responseName).item(0)  
                  .getTextContent();  
          return ret;  
      } catch (Exception e) {  
          e.printStackTrace();  
          throw e;  
      }  
  }  
}  