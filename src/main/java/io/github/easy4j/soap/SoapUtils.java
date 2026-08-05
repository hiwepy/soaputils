/*
 * SoapUI, Copyright (C) 2004-2017 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence"); 
 * You may not use this work except in compliance with the Licence. 
 * You may obtain a copy of the Licence at: 
 * 
 * http://ec.europa.eu/idabc/eupl 
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is 
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the Licence for the specific language governing permissions and limitations 
 * under the Licence. 
 */

package io.github.easy4j.soap;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.BindingOperation;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.eviware.soapui.SoapUI;
import io.github.easy4j.soap.utils.XmlUtils;

/**
 * SOAP-related utility-methods..
 *
 * @author ole.matzura
 */

public class SoapUtils {
    public static boolean isSoapFault(String responseContent, SoapVersion soapVersion) throws XmlException {
        if (StringUtils.isEmpty(responseContent)) {
            return false;
        }

        // check manually before resource intensive xpath
        if (responseContent.indexOf(":Fault") > 0 || responseContent.indexOf("<Fault") > 0) {
            // XmlObject xml = XmlObject.Factory.parse( responseContent );
            XmlObject xml = XmlUtils.createXmlObject(responseContent);
            XmlObject[] paths = xml.selectPath("declare namespace env='" + soapVersion.getEnvelopeNamespace() + "';"
                    + "//env:Fault");
            if (paths.length > 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSoapFault(String responseContent) throws XmlException {
        return isSoapFault(responseContent, SoapVersion.Soap12) || isSoapFault(responseContent, SoapVersion.Soap11);
    }

    /**
     * Init soapversion from content-type header.. should envelope be checked
     * and/or override?
     *
     * @param xmlObject
     */

    public static SoapVersion deduceSoapVersion(String contentType, XmlObject xmlObject) {
        if (xmlObject != null) {
            Element elm = ((Document) (xmlObject.getDomNode())).getDocumentElement();
            if (elm.getLocalName().equals("Envelope")) {
                if (elm.getNamespaceURI().equals(SoapVersion.Soap11.getEnvelopeNamespace())) {
                    return SoapVersion.Soap11;
                } else if (elm.getNamespaceURI().equals(SoapVersion.Soap12.getEnvelopeNamespace())) {
                    return SoapVersion.Soap12;
                }
            }
        }

        SoapVersion soapVersion = null;

        if (StringUtils.isEmpty(contentType)) {
            return null;
        }

        soapVersion = contentType.startsWith(SoapVersion.Soap11.getContentType()) ? SoapVersion.Soap11 : null;
        soapVersion = soapVersion == null && contentType.startsWith(SoapVersion.Soap12.getContentType()) ? SoapVersion.Soap12
                : soapVersion;
        if (soapVersion == null && contentType.startsWith("application/xop+xml")) {
            if (contentType.indexOf("type=\"" + SoapVersion.Soap11.getContentType() + "\"") > 0) {
                soapVersion = SoapVersion.Soap11;
            } else if (contentType.indexOf("type=\"" + SoapVersion.Soap12.getContentType() + "\"") > 0) {
                soapVersion = SoapVersion.Soap12;
            }
        }

        return soapVersion;
    }

    public static XmlObject getBodyElement(XmlObject messageObject, SoapVersion soapVersion) throws XmlException {
        XmlObject[] envelope = messageObject.selectChildren(soapVersion.getEnvelopeQName());
        if (envelope.length != 1) {
            throw new XmlException("Missing/Invalid SOAP Envelope, expecting [" + soapVersion.getEnvelopeQName() + "]");
        }

        XmlObject[] body = envelope[0].selectChildren(soapVersion.getBodyQName());
        if (body.length != 1) {
            throw new XmlException("Missing/Invalid SOAP Body, expecting [" + soapVersion.getBodyQName() + "]");
        }

        return body[0];
    }

    public static XmlObject getHeaderElement(XmlObject messageObject, SoapVersion soapVersion, boolean create)
            throws XmlException {
        XmlObject[] envelope = messageObject.selectChildren(soapVersion.getEnvelopeQName());
        if (envelope.length != 1) {
            throw new XmlException("Missing/Invalid SOAP Envelope, expecting [" + soapVersion.getEnvelopeQName() + "]");
        }

        QName headerQName = soapVersion.getHeaderQName();
        XmlObject[] header = envelope[0].selectChildren(headerQName);
        if (header.length == 0 && create) {
            Element elm = (Element) envelope[0].getDomNode();
            Element headerElement = elm.getOwnerDocument().createElementNS(headerQName.getNamespaceURI(),
                    headerQName.getLocalPart());

            elm.insertBefore(headerElement, elm.getFirstChild());

            header = envelope[0].selectChildren(headerQName);
        }

        return header.length == 0 ? null : header[0];
    }

    public static XmlObject getContentElement(XmlObject messageObject, SoapVersion soapVersion) throws XmlException {
        if (messageObject == null) {
            return null;
        }

        XmlObject bodyElement = SoapUtils.getBodyElement(messageObject, soapVersion);
        if (bodyElement != null) {
            XmlCursor cursor = bodyElement.newCursor();

            try {
                if (cursor.toFirstChild()) {
                    while (!cursor.isContainer()) {
                        cursor.toNextSibling();
                    }

                    if (cursor.isContainer()) {
                        return cursor.getObject();
                    }
                }
            } catch (Exception e) {
                SoapUI.logError(e);
            } finally {
                cursor.dispose();
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static String removeEmptySoapHeaders(String content, SoapVersion soapVersion) throws XmlException {
        // XmlObject xmlObject = XmlObject.Factory.parse( content );
        XmlObject xmlObject = XmlUtils.createXmlObject(content);
        XmlObject[] selectPath = xmlObject.selectPath("declare namespace soap='" + soapVersion.getEnvelopeNamespace()
                + "';/soap:Envelope/soap:Header");
        if (selectPath.length > 0) {
            Node domNode = selectPath[0].getDomNode();
            if (!domNode.hasChildNodes() && !domNode.hasAttributes()) {
                domNode.getParentNode().removeChild(domNode);
                return xmlObject.xmlText();
            }
        }

        return content;
    }

    public static SoapVersion deduceSoapVersion(String requestContentType, String requestContent) {
        try {
            // return deduceSoapVersion( requestContentType,
            // XmlObject.Factory.parse( requestContent ) );
            return deduceSoapVersion(requestContentType, XmlUtils.createXmlObject(requestContent));
        } catch (XmlException e) {
            return deduceSoapVersion(requestContentType, (XmlObject) null);
        }
    }

    public static String transferSoapHeaders(String requestContent, String newRequest, SoapVersion soapVersion) {
        try {
            // XmlObject source = XmlObject.Factory.parse( requestContent );
            XmlObject source = XmlUtils.createXmlObject(requestContent);
            String headerXPath = "declare namespace ns='" + soapVersion.getEnvelopeNamespace() + "'; //ns:Header";
            XmlObject[] header = source.selectPath(headerXPath);
            if (header.length == 1) {
                Element headerElm = (Element) header[0].getDomNode();
                NodeList childNodes = headerElm.getChildNodes();
                if (childNodes.getLength() > 0) {
                    // XmlObject dest = XmlObject.Factory.parse( newRequest );
                    XmlObject dest = XmlUtils.createXmlObject(newRequest);
                    header = dest.selectPath(headerXPath);
                    Element destElm = null;

                    if (header.length == 0) {
                        Element docElm = ((Document) dest.getDomNode()).getDocumentElement();

                        destElm = (Element) docElm.insertBefore(
                                docElm.getOwnerDocument().createElementNS(soapVersion.getEnvelopeNamespace(),
                                        docElm.getPrefix() + ":Header"),
                                XmlUtils.getFirstChildElementNS(docElm, soapVersion.getBodyQName()));
                    } else {
                        destElm = (Element) header[0].getDomNode();
                    }

                    for (int c = 0; c < childNodes.getLength(); c++) {
                        Node childNode = childNodes.item(c);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            if (XmlUtils.getFirstChildElementNS(destElm, childNode.getNamespaceURI(),
                                    childNode.getLocalName()) != null) {
                                continue;
                            }

                            destElm.appendChild(destElm.getOwnerDocument().importNode(childNode, true));
                        }
                    }

                    return dest.xmlText();
                }
            }
        } catch (XmlException e) {
            SoapUI.logError(e);
        }

        return newRequest;
    }
}
