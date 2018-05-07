/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package com.github.vindell.soap;

import java.util.HashMap;  

public class Test {  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        // 该WebService文档==>http://ws.webxml.com.cn/webservices/DomesticAirline.asmx  
        WebServiceClient ws = new WebServiceClient("http://WebXml.com.cn/",  
                "http://ws.webxml.com.cn/webservices/DomesticAirline.asmx",  
                "DomesticAirline", "DomesticAirlineSoap12",  
                "getDomesticAirlinesTime", "getDomesticAirlinesTimeResult");  
        HashMap<String, String> inMsg = new HashMap<String, String>();  
        inMsg.put("startCity", "宁波");  
        inMsg.put("lastCity", "青岛");  
        inMsg.put("theDate", "2017-05-11");  
  
        try {  
            String ret = ws.sendMessage(inMsg);  
            System.out.println(ret.toString()); // 没有对结果做处理  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  