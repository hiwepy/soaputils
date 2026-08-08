package io.github.easy4j.soap.type;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link NumberSoapType}.
 */
public class NumberSoapTypeTest {

    private final NumberSoapType type = new NumberSoapType();

    @Test
    public void shouldReturnNumberBeanTypes() {
        Class<?>[] types = type.getBeanTypes();
        assertEquals(11, types.length);
        assertEquals(Integer.TYPE, types[0]);
        assertEquals(BigDecimal.class, types[10]);
    }

    @Test
    public void shouldReturnNumberSoapTypes() {
        String[] types = type.getSoapTypes();
        assertEquals(4, types.length);
    }

    @Test
    public void shouldSetIntegerValue() throws Exception {
        SOAPElement elem = createElement();
        type.setValue(elem, 42, Integer.class);
        assertEquals("42", elem.getTextContent());
    }

    @Test
    public void shouldSetLongValue() throws Exception {
        SOAPElement elem = createElement();
        type.setValue(elem, 100L, Long.class);
        assertEquals("100", elem.getTextContent());
    }

    @Test
    public void shouldConvertToInteger() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("123");
        Object result = type.convertToBean(Integer.class, new SOAPElement[]{elem});
        assertEquals(Integer.valueOf(123), result);
    }

    @Test
    public void shouldConvertToLong() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("456");
        Object result = type.convertToBean(Long.class, new SOAPElement[]{elem});
        assertEquals(Long.valueOf(456), result);
    }

    @Test
    public void shouldConvertToShort() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("7");
        Object result = type.convertToBean(Short.class, new SOAPElement[]{elem});
        assertEquals(Short.valueOf((short) 7), result);
    }

    @Test
    public void shouldConvertToDouble() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("3.14");
        Object result = type.convertToBean(Double.class, new SOAPElement[]{elem});
        assertEquals(Double.valueOf(3.14), result);
    }

    @Test
    public void shouldConvertToFloat() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("2.5");
        Object result = type.convertToBean(Float.class, new SOAPElement[]{elem});
        assertEquals(Float.valueOf(2.5f), result);
    }

    @Test
    public void shouldConvertToBigDecimal() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("99.99");
        Object result = type.convertToBean(BigDecimal.class, new SOAPElement[]{elem});
        assertTrue(result instanceof BigDecimal);
    }

    @Test
    public void shouldReturnStringForUnknownType() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("text");
        Object result = type.convertToBean(String.class, new SOAPElement[]{elem});
        assertEquals("text", result);
    }

    private SOAPElement createElement() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        return message.getSOAPPart().getEnvelope().getBody().addChildElement("value");
    }
}
