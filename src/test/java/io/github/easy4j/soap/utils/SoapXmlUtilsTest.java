package io.github.easy4j.soap.utils;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests for {@link SoapXmlUtils}.
 */
public class SoapXmlUtilsTest {

    @Test
    public void shouldGetNameFromNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElementNS("http://ns", "local");
        assertEquals("local", SoapXmlUtils.getName(elem));
    }

    @Test
    public void shouldGetNameFromElement() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElementNS("http://ns", "prefix:local");
        assertEquals("local", SoapXmlUtils.getName(elem));
    }

    @Test
    public void shouldGetNameFallbackToNodeName() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("simpleName");
        assertEquals("simpleName", SoapXmlUtils.getName((Node) elem));
    }

    @Test
    public void shouldGetNameFallbackToTagName() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("tagName");
        assertEquals("tagName", SoapXmlUtils.getName(elem));
    }

    @Test
    public void shouldCountElementsBefore() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);

        Element c1 = doc.createElement("target");
        Element c2 = doc.createElement("other");
        Element c3 = doc.createElement("target");
        root.appendChild(c1);
        root.appendChild(c2);
        root.appendChild(c3);

        assertEquals(0, SoapXmlUtils.countElementsBefore(c1, "target"));
        assertEquals(1, SoapXmlUtils.countElementsBefore(c3, "target"));
    }

    @Test
    public void shouldCountChildElementsOfType() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createElement("a"));
        root.appendChild(doc.createTextNode("text"));
        root.appendChild(doc.createElement("b"));

        assertEquals(2, SoapXmlUtils.countChildElementsOfType(root, Node.ELEMENT_NODE));
        assertEquals(1, SoapXmlUtils.countChildElementsOfType(root, Node.TEXT_NODE));
    }

    @Test
    public void shouldGetFirstChildByType() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createTextNode("text"));
        root.appendChild(doc.createElement("child"));

        Node textNode = SoapXmlUtils.getFirstChildByType(root, Node.TEXT_NODE);
        assertNotNull(textNode);
        assertEquals(Node.TEXT_NODE, textNode.getNodeType());

        Node elemNode = SoapXmlUtils.getFirstChildByType(root, Node.ELEMENT_NODE);
        assertNotNull(elemNode);
        assertEquals("child", elemNode.getNodeName());
    }

    @Test
    public void shouldReturnNullWhenNoChildOfType() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);

        assertNull(SoapXmlUtils.getFirstChildByType(root, Node.COMMENT_NODE));
    }

    @Test
    public void shouldDetectCollectionByComment() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Comment comment = doc.createComment("Zero or more repetitions:");
        root.appendChild(comment);

        assertTrue(SoapXmlUtils.assertIsCollection(root));
    }

    @Test
    public void shouldNotDetectCollectionWithoutComment() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);

        assertFalse(SoapXmlUtils.assertIsCollection(root));
    }

    @Test
    public void shouldNotDetectCollectionWithOtherComment() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createComment("Some other comment"));

        assertFalse(SoapXmlUtils.assertIsCollection(root));
    }

    private Document newDoc() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.newDocument();
    }
}
