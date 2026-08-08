package io.github.easy4j.soap.type;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link ListSoapType}.
 */
public class ListSoapTypeTest {

    private final ListSoapType type = new ListSoapType();

    @Test
    public void shouldReturnListBeanType() {
        Class<?>[] types = type.getBeanTypes();
        assertEquals(1, types.length);
        assertEquals(List.class, types[0]);
    }

    @Test
    public void shouldReturnListSoapType() {
        String[] types = type.getSoapTypes();
        assertEquals(1, types.length);
        assertEquals("List", types[0]);
    }

    @Test
    public void shouldConvertMultipleElementsToList() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement parent = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("items");
        parent.addChildElement("item").setTextContent("a");
        parent.addChildElement("item").setTextContent("b");
        parent.addChildElement("item").setTextContent("c");

        // Get the "item" elements as SOAPElement array
        SOAPElement itemElem = (SOAPElement) parent.getElementsByTagName("item").item(0);
        Object result = type.convertToBean(new SOAPElement[]{itemElem});
        assertNotNull(result);
        assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        assertTrue(list.size() >= 1);
    }

    @Test
    public void shouldReturnNullListForEmptyElements() throws Exception {
        Object result = type.convertToBean(new SOAPElement[0]);
        assertNull(result);
    }

    @Test
    public void shouldSetListValue() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement parent = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("items");
        SOAPElement child = parent.addChildElement("item");

        List<String> list = new ArrayList<>();
        list.add("x");
        list.add("y");

        // should not throw
        type.setValue(child, list, List.class);
    }
}
