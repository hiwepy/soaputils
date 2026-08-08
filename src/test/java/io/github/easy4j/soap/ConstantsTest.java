package io.github.easy4j.soap;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link Constants} namespace constant values.
 */
public class ConstantsTest {

    @Test
    public void shouldHaveCorrectXsdNamespace() {
        assertEquals("http://www.w3.org/2001/XMLSchema", Constants.XSD_NS);
    }

    @Test
    public void shouldHaveCorrectXmlNamespace() {
        assertEquals("http://www.w3.org/2000/xmlns/", Constants.XML_NS);
    }

    @Test
    public void shouldHaveCorrectWsdl11Namespace() {
        assertEquals("http://schemas.xmlsoap.org/wsdl/", Constants.WSDL11_NS);
    }

    @Test
    public void shouldHaveCorrectSoapEncodingNamespace() {
        assertEquals("http://schemas.xmlsoap.org/soap/encoding/", Constants.SOAP_ENCODING_NS);
    }

    @Test
    public void shouldHaveCorrectSoap11EnvelopeNamespace() {
        assertEquals("http://schemas.xmlsoap.org/soap/envelope/", Constants.SOAP11_ENVELOPE_NS);
    }

    @Test
    public void shouldHaveCorrectSoapHttpTransport() {
        assertEquals("http://schemas.xmlsoap.org/soap/http", Constants.SOAP_HTTP_TRANSPORT);
    }

    @Test
    public void shouldHaveCorrectSoapHttpBindingNamespace() {
        assertEquals("http://schemas.xmlsoap.org/wsdl/soap/", Constants.SOAP_HTTP_BINDING_NS);
    }

    @Test
    public void shouldHaveCorrectSoap12HttpBindingNamespace() {
        assertEquals("http://www.w3.org/2003/05/soap/bindings/HTTP/", Constants.SOAP12_HTTP_BINDING_NS);
    }

    @Test
    public void shouldHaveCorrectXsiNamespace() {
        assertEquals("http://www.w3.org/2001/XMLSchema-instance", Constants.XSI_NS);
    }

    @Test
    public void shouldHaveCorrectXsi2000Namespace() {
        assertEquals("http://www.w3.org/2000/XMLSchema-instance", Constants.XSI_NS_2000);
    }

    @Test
    public void shouldHaveCorrectSoap12EnvelopeNamespace() {
        assertEquals("http://www.w3.org/2003/05/soap-envelope", Constants.SOAP12_ENVELOPE_NS);
    }

    @Test
    public void shouldHaveCorrectWadl10Namespace() {
        assertEquals("http://research.sun.com/wadl/2006/10", Constants.WADL10_NS);
    }

    @Test
    public void shouldHaveCorrectWadl11Namespace() {
        assertEquals("http://wadl.dev.java.net/2009/02", Constants.WADL11_NS);
    }

    @Test
    public void shouldHaveCorrectSoapMicrosoftTcp() {
        assertEquals("http://schemas.microsoft.com/wse/2003/06/tcp", Constants.SOAP_MICROSOFT_TCP);
    }
}
