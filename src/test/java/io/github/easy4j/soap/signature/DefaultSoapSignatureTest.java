package io.github.easy4j.soap.signature;

import static org.junit.Assert.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

/**
 * Tests for {@link DefaultSoapSignature}.
 */
public class DefaultSoapSignatureTest {

    private final DefaultSoapSignature signature = new DefaultSoapSignature();

    @Test
    public void shouldAddUsernameHeaderElement() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPHeader header = message.getSOAPPart().getEnvelope().getHeader();

        signature.sign(header, "http://example.com/service");

        boolean found = false;
        var it = header.getChildElements();
        while (it.hasNext()) {
            Object child = it.next();
            if (child instanceof javax.xml.soap.SOAPElement) {
                javax.xml.soap.SOAPElement elem = (javax.xml.soap.SOAPElement) child;
                if ("username".equals(elem.getLocalName())) {
                    assertEquals("huoyangege", elem.getValue());
                    found = true;
                }
            }
        }
        assertTrue("Expected username header element", found);
    }

    @Test
    public void shouldNotModifyBody() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();

        // should not throw
        signature.sign(body, "http://example.com/service");
    }
}
