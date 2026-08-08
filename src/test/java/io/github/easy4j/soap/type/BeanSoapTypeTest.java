package io.github.easy4j.soap.type;

import static org.junit.Assert.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link BeanSoapType}.
 */
public class BeanSoapTypeTest {

    private final BeanSoapType type = new BeanSoapType();

    public static class SampleBean {
        private String name;
        private int age;

        public SampleBean() {}
    }

    @Test
    public void shouldReturnObjectBeanType() {
        Class<?>[] types = type.getBeanTypes();
        assertEquals(1, types.length);
        assertEquals(Object.class, types[0]);
    }

    @Test
    public void shouldReturnAnyTypeSoapType() {
        String[] types = type.getSoapTypes();
        assertEquals(1, types.length);
        assertEquals("anyType", types[0]);
    }

    @Test
    public void shouldSetBeanFieldValues() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement root = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("bean");
        root.addChildElement("name").setTextContent("John");
        root.addChildElement("age").setTextContent("30");

        SampleBean bean = new SampleBean();
        bean.name = "John";
        bean.age = 30;

        type.setValue(root, bean, SampleBean.class);
        // Should not throw and should process fields
    }

    @Test
    public void shouldConvertElementToBean() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement root = message.getSOAPPart().getEnvelope().getBody()
                .addChildElement("bean");
        root.addChildElement("name").setTextContent("Jane");
        root.addChildElement("age").setTextContent("25");

        Object result = type.convertToBean(SampleBean.class, new SOAPElement[]{root});
        assertNotNull(result);
        assertTrue(result instanceof SampleBean);
        SampleBean bean = (SampleBean) result;
        assertEquals("Jane", bean.name);
    }

    @Test
    public void shouldReturnNullListForEmptyElements() throws Exception {
        Object result = type.convertToBean(SampleBean.class, new SOAPElement[0]);
        assertNull(result);
    }

    @Test
    public void shouldReturnListWhenMultipleElements() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPElement body = message.getSOAPPart().getEnvelope().getBody();
        SOAPElement e1 = body.addChildElement("item");
        e1.addChildElement("name").setTextContent("A");
        SOAPElement e2 = body.addChildElement("item2");
        e2.addChildElement("name").setTextContent("B");

        Object result = type.convertToBean(SampleBean.class, new SOAPElement[]{e1, e2});
        assertNotNull(result);
        assertTrue(result instanceof java.util.List);
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size());
    }
}
