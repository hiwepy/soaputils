package io.github.easy4j.soap.type;

import static org.junit.Assert.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link StringSoapType}.
 */
public class StringSoapTypeTest {

    private final StringSoapType type = new StringSoapType();

    @Test
    public void shouldReturnStringBeanType() {
        Class<?>[] types = type.getBeanTypes();
        assertEquals(1, types.length);
        assertEquals(String.class, types[0]);
    }

    @Test
    public void shouldReturnStringSoapType() {
        String[] types = type.getSoapTypes();
        assertEquals(1, types.length);
        assertEquals("string", types[0]);
    }

    @Test
    public void shouldSetValueOnElement() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement element = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("test");

        type.setValue(element, "hello", String.class);
        assertEquals("hello", element.getTextContent());
    }

    @Test
    public void shouldConvertElementToBean() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement element = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("test");
        element.setTextContent("world");

        Object result = type.convertToBean(String.class, new SOAPElement[]{element});
        assertEquals("world", result);
    }

    @Test
    public void shouldConvertWithDefaultType() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement element = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("test");
        element.setTextContent("default");

        Object result = type.convertToBean(new SOAPElement[]{element});
        assertEquals("default", result);
    }

    @Test
    public void shouldSetWithDefaultType() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement element = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("test");

        type.setValue(element, "viaDefault");
        assertEquals("viaDefault", element.getTextContent());
    }
}
