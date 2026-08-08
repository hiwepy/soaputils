package io.github.easy4j.soap.handler;

import static org.junit.Assert.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link PlainTextResponseHandler}.
 */
public class PlainTextResponseHandlerTest {

    private final PlainTextResponseHandler handler = new PlainTextResponseHandler();

    @Test
    public void shouldReturnNullForHandleResponse() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        assertNull(handler.handleResponse(message));
    }

    @Test
    public void shouldDoNothingOnPreHandle() throws Exception {
        handler.preHandle(null);
    }
}
