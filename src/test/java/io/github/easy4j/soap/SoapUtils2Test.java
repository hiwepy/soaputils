package io.github.easy4j.soap;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Tests for {@link SoapUtils2}.
 */
public class SoapUtils2Test {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenObjectIsNull() {
        SoapUtils2.notNull(null, "must not be null");
    }

    @Test
    public void shouldPassWhenObjectIsNotNull() {
        SoapUtils2.notNull("value", "must not be null");
        // no exception means pass
    }

    @Test
    public void shouldReturnAttributeFromNode() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element elem = doc.createElement("test");
        elem.setAttribute("myAttr", "myValue");
        doc.appendChild(elem);

        assertEquals("myValue", SoapUtils2.getAttribute(elem, "myAttr"));
    }

    @Test
    public void shouldReturnNullForMissingAttribute() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);

        assertNull(SoapUtils2.getAttribute(elem, "missing"));
    }

    @Test
    public void shouldHaveDefaultHandler() {
        assertNotNull(SoapUtils2.DEFAULT_HANDLER);
    }

    @Test
    public void shouldHaveDefaultSignature() {
        assertNotNull(SoapUtils2.DEFAULT_SIGNATURE);
    }
}
