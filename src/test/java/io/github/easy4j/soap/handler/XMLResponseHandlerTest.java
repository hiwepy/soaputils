package io.github.easy4j.soap.handler;

import static org.junit.Assert.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests for {@link XMLResponseHandler}.
 */
public class XMLResponseHandlerTest {

    private final XMLResponseHandler handler = new XMLResponseHandler();

    @Test
    public void shouldExtractBodyAsDocument() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        body.addChildElement(factory.createName("getResult")).addTextNode("value");
        message.saveChanges();

        Document doc = handler.handleResponse(message);
        assertNotNull(doc);
    }

    @Test
    public void shouldDoNothingOnPreHandle() throws Exception {
        handler.preHandle(null);
    }
}
