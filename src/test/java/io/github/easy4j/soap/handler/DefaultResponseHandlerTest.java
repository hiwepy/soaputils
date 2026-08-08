package io.github.easy4j.soap.handler;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

import io.github.easy4j.soap.exception.InvokeException;

/**
 * Tests for {@link DefaultResponseHandler}.
 */
public class DefaultResponseHandlerTest {

    private final DefaultResponseHandler handler = new DefaultResponseHandler();

    @Test
    public void shouldReturnMessageWhenNoFault() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        body.addChildElement(factory.createName("result")).addTextNode("ok");
        message.saveChanges();

        SOAPMessage result = handler.handleResponse(message);
        assertNotNull(result);
        assertSame(message, result);
    }

    @Test(expected = InvokeException.class)
    public void shouldThrowWhenFaultPresent() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        SOAPBody faultBody = message.getSOAPPart().getEnvelope().getBody();
        javax.xml.soap.SOAPFault fault = faultBody.addFault();
        fault.setFaultCode("S:Server");
        fault.setFaultString("Something went wrong");
        message.saveChanges();

        handler.handleResponse(message);
    }

    @Test
    public void shouldDoNothingOnPreHandle() throws Exception {
        // preHandle is a no-op; verify it doesn't throw
        handler.preHandle(null);
    }
}
