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

import javax.xml.namespace.QName;  
import javax.xml.soap.MessageFactory;  
import javax.xml.soap.SOAPBody;  
import javax.xml.soap.SOAPBodyElement;  
import javax.xml.soap.SOAPConstants;  
import javax.xml.soap.SOAPEnvelope;  
import javax.xml.soap.SOAPMessage;  
import javax.xml.ws.Dispatch;  
import javax.xml.ws.Service;

import org.junit.Test;
import org.w3c.dom.Document;

public class Soap_Test {

	@Test
	public void main() throws Exception {  
		
        // {*} * 为图片中的数字  
        String ns = "http://tempuri.org/";  // {1}  
        String wsdlUrl = "http://work.dahuatech.com/Application/Service/MsgSmsService.asmx?WSDL";  // {2}  
        //1、创建服务(Service)    
        URL url = new URL(wsdlUrl);    
        QName sname = new QName(ns, "MsgSmsService"); // {3}  
        Service service = Service.create(url, sname);  
                            
        //2、创建Dispatch    
        Dispatch<SOAPMessage> dispatch = service.createDispatch(new QName(ns, "MsgSmsServiceSoap12"), SOAPMessage.class, Service.Mode.MESSAGE); // {4}    
                        
        //3、创建SOAPMessage    
        SOAPMessage msg = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();    
        SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();    
        SOAPBody body = envelope.getBody();    
                        
        //4、创建QName来指定消息中传递数据    
        QName ename = new QName(ns, "SendPlmMsg", "tem");//<nn:add xmlns="xx"/>  // {5}  
        SOAPBodyElement ele = body.addBodyElement(ename);    
        // 传递参数    
        // {6}  
        ele.addChildElement("strMobiles", "tem").setValue("151****3701");      
        ele.addChildElement("strMSg", "tem").setValue("测试!");      
        msg.writeTo(System.out);    
        System.out.println("\n invoking.....");    
                                
        //5、通过Dispatch传递消息,会返回响应消息    
        SOAPMessage response = dispatch.invoke(msg);    
        response.writeTo(System.out);    
        System.out.println();    
                        
        //6、响应消息处理,将响应的消息转换为dom对象    
        Document doc = response.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();    
        String str = doc.getElementsByTagName("SendPlmMsgResult").item(0).getTextContent();  // {7}  
        System.out.println(str);    
    }  
	
}
