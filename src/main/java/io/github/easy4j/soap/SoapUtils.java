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
 * General-purpose SOAP utility methods for inspecting, parsing and
 * transforming SOAP XML content. Includes helpers for fault detection,
 * SOAP version deduction, body/header extraction, and header transfer
 * between SOAP messages.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see SoapVersion
 * @see io.github.easy4j.soap.utils.XmlUtils
 */

public class SoapUtils {
    /**
     * Checks whether the given response content contains a SOAP fault for
     * the specified SOAP version.
     *
     * @param responseContent the XML string of the SOAP response
     * @param soapVersion     the SOAP version to check against
     * @return {@code true} if a {@code Fault} element is found in the envelope body
     * @throws XmlException if the response content cannot be parsed as XML
     */
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

    /**
     * Checks whether the given response content contains a SOAP fault,
     * trying both SOAP 1.2 and SOAP 1.1 namespaces.
     *
     * @param responseContent the XML string of the SOAP response
     * @return {@code true} if a fault is detected under either SOAP version
     * @throws XmlException if the response content cannot be parsed as XML
     */
    public static boolean isSoapFault(String responseContent) throws XmlException {
        return isSoapFault(responseContent, SoapVersion.Soap12) || isSoapFault(responseContent, SoapVersion.Soap11);
    }

    /**
     * Deduces the SOAP version from the content-type header and/or the
     * parsed XML object. The XML envelope namespace takes precedence;
     * the content-type header is used as a fallback.
     *
     * @param contentType the HTTP Content-Type header value (may be {@code null})
     * @param xmlObject   the parsed XML object of the SOAP message (may be {@code null})
     * @return the deduced {@link SoapVersion}, or {@code null} if it cannot be determined
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

    /**
     * Extracts the SOAP Body element from the given message object.
     *
     * @param messageObject the parsed SOAP message XML object
     * @param soapVersion   the SOAP version that defines the envelope/body QNames
     * @return the Body {@link XmlObject}
     * @throws XmlException if the envelope or body element is missing or invalid
     */
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

    /**
     * Extracts the SOAP Header element from the given message object.
     * Optionally creates the header element if it does not exist.
     *
     * @param messageObject the parsed SOAP message XML object
     * @param soapVersion   the SOAP version that defines the header QName
     * @param create        if {@code true} and no header exists, one will be created
     * @return the Header {@link XmlObject}, or {@code null} if not present and not created
     * @throws XmlException if the envelope element is missing or invalid
     */
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

    /**
     * Extracts the first child container element inside the SOAP Body,
     * typically the method-response element.
     *
     * @param messageObject the parsed SOAP message XML object (may be {@code null})
     * @param soapVersion   the SOAP version for body extraction
     * @return the content {@link XmlObject}, or {@code null} if not found
     * @throws XmlException if the body cannot be extracted
     */
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

    /**
     * Removes the SOAP Header element from the given XML content if it
     * is empty (has no child nodes and no attributes).
     *
     * @param content     the XML string of the SOAP message
     * @param soapVersion the SOAP version to determine the header namespace
     * @return the XML content with the empty header removed, or the
     *         original content if the header is not empty
     * @throws XmlException if the content cannot be parsed as XML
     */
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

    /**
     * Deduces the SOAP version from the content-type header and request
     * XML content string. Falls back to content-type only if parsing fails.
     *
     * @param requestContentType the HTTP Content-Type header value
     * @param requestContent     the XML string of the SOAP request
     * @return the deduced {@link SoapVersion}, or {@code null} if it cannot be determined
     */
    public static SoapVersion deduceSoapVersion(String requestContentType, String requestContent) {
        try {
            // return deduceSoapVersion( requestContentType,
            // XmlObject.Factory.parse( requestContent ) );
            return deduceSoapVersion(requestContentType, XmlUtils.createXmlObject(requestContent));
        } catch (XmlException e) {
            return deduceSoapVersion(requestContentType, (XmlObject) null);
        }
    }

    /**
     * Transfers SOAP header elements from one request content into another.
     * Header elements present in {@code requestContent} but absent in
     * {@code newRequest} are copied over.
     *
     * @param requestContent the source XML string containing headers to transfer
     * @param newRequest     the target XML string to receive the headers
     * @param soapVersion    the SOAP version for namespace resolution
     * @return the updated {@code newRequest} XML with transferred headers,
     *         or the original {@code newRequest} if transfer is not possible
     */
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
