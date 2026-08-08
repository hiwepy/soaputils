package io.github.easy4j.soap.type;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link DateSoapType}.
 */
public class DateSoapTypeTest {

    private final DateSoapType type = new DateSoapType();

    @Test
    public void shouldReturnDateBeanTypes() {
        Class<?>[] types = type.getBeanTypes();
        assertEquals(2, types.length);
        assertEquals(Date.class, types[0]);
        assertEquals(Calendar.class, types[1]);
    }

    @Test
    public void shouldReturnDateSoapTypes() {
        String[] types = type.getSoapTypes();
        assertEquals(2, types.length);
        assertEquals("date", types[0]);
        assertEquals("dateTime", types[1]);
    }

    @Test
    public void shouldSetDateValue() throws Exception {
        SOAPElement elem = createElement();
        Date now = new Date();
        type.setValue(elem, now, Date.class);
        assertNotNull(elem.getTextContent());
        assertTrue(elem.getTextContent().length() > 0);
    }

    @Test
    public void shouldSetCalendarValue() throws Exception {
        SOAPElement elem = createElement();
        Calendar cal = Calendar.getInstance();
        type.setValue(elem, cal, Calendar.class);
        assertNotNull(elem.getTextContent());
        assertTrue(elem.getTextContent().length() > 0);
    }

    @Test
    public void shouldConvertDateValue() throws Exception {
        SOAPElement elem = createElement();
        Date now = new Date();
        type.setValue(elem, now, Date.class);

        Object result = type.convertToBean(Date.class, new SOAPElement[]{elem});
        assertTrue(result instanceof Date);
    }

    @Test
    public void shouldConvertCalendarValue() throws Exception {
        SOAPElement elem = createElement();
        Calendar cal = Calendar.getInstance();
        type.setValue(elem, cal, Calendar.class);

        Object result = type.convertToBean(Calendar.class, new SOAPElement[]{elem});
        assertTrue(result instanceof Calendar);
    }

    @Test
    public void shouldReturnStringForUnknownType() throws Exception {
        SOAPElement elem = createElement();
        elem.setTextContent("2024-01-01");

        Object result = type.convertToBean(String.class, new SOAPElement[]{elem});
        assertEquals("2024-01-01", result);
    }

    private SOAPElement createElement() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        return message.getSOAPPart().getEnvelope().getBody().addChildElement("dateValue");
    }
}
