package io.github.easy4j.soap.utils;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Tests for {@link XmlUtils}.
 */
public class XmlUtilsTest {

    // --- parse ---
    @Test
    public void shouldParseInputStream() throws Exception {
        String xml = "<root><child>text</child></root>";
        Document doc = XmlUtils.parse(new ByteArrayInputStream(xml.getBytes()));
        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getNodeName());
    }

    @Test
    public void shouldReturnNullForInvalidInputStream() {
        Document doc = XmlUtils.parse(new ByteArrayInputStream("not-xml".getBytes()));
        assertNull(doc);
    }

    // --- entitize ---
    @Test
    public void shouldEntitizeAllSpecialChars() {
        assertEquals("&amp;&lt;&gt;&quot;&apos;", XmlUtils.entitize("&<>\"'"));
    }

    @Test
    public void shouldEntitizeContent() {
        assertEquals("&amp;&quot;&apos;", XmlUtils.entitizeContent("&\"'"));
    }

    // --- createXmlObject(String) ---
    @Test
    public void shouldCreateXmlObjectFromString() throws Exception {
        XmlObject xml = XmlUtils.createXmlObject("<root/>");
        assertNotNull(xml);
    }

    @Test(expected = Exception.class)
    public void shouldThrowForInvalidXmlString() throws Exception {
        XmlUtils.createXmlObject("<not closed");
    }

    // --- createXmlObject(Node) ---
    @Test
    public void shouldCreateXmlObjectFromNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("root");
        doc.appendChild(elem);
        XmlObject xml = XmlUtils.createXmlObject(elem);
        assertNotNull(xml);
    }

    // --- createXmlObject(URL) ---
    @Test(expected = XmlException.class)
    public void shouldThrowForInvalidUrl() throws Exception {
        XmlUtils.createXmlObject(new URL("file:///nonexistent.xml"));
    }

    // --- createXmlObject(File) ---
    @Test(expected = XmlException.class)
    public void shouldThrowForInvalidFile() throws Exception {
        XmlUtils.createXmlObject(new File("/nonexistent.xml"));
    }

    // --- serializePretty(Document) ---
    @Test
    public void shouldSerializeDocumentPretty() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        root.setTextContent("value");
        doc.appendChild(root);
        String result = XmlUtils.serializePretty(doc);
        assertNotNull(result);
        assertTrue(result.contains("root"));
    }

    // --- serializePretty(Document, Writer) ---
    @Test
    public void shouldSerializeDocumentToWriter() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        StringWriter writer = new StringWriter();
        XmlUtils.serializePretty(doc, writer);
        assertTrue(writer.toString().contains("root"));
    }

    // --- serializePretty(XmlObject, Writer) ---
    @Test
    public void shouldSerializeXmlObjectToWriter() throws Exception {
        XmlObject xml = XmlUtils.createXmlObject("<root><child/></root>");
        StringWriter writer = new StringWriter();
        XmlUtils.serializePretty(xml, writer);
        assertTrue(writer.toString().contains("root"));
    }

    // --- serialize(Document) ---
    @Test
    public void shouldSerializeDocument() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        String result = XmlUtils.serialize(doc);
        assertNotNull(result);
        assertTrue(result.contains("root"));
    }

    // --- serialize(Node, boolean) ---
    @Test
    public void shouldSerializeNodePrettyPrint() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        String result = XmlUtils.serialize(root, true);
        assertNotNull(result);
        assertTrue(result.contains("root"));
    }

    @Test
    public void shouldSerializeNodeNotPretty() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        String result = XmlUtils.serialize(root, false);
        assertNotNull(result);
    }

    // --- serialize(Document, Writer) ---
    @Test
    public void shouldSerializeDocumentToWriterPlain() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        StringWriter writer = new StringWriter();
        XmlUtils.serialize(doc, writer);
        assertTrue(writer.toString().contains("root"));
    }

    // --- serialize(Element, Writer) ---
    @Test
    public void shouldSerializeElementToWriter() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        StringWriter writer = new StringWriter();
        XmlUtils.serialize(root, writer);
        assertTrue(writer.toString().contains("root"));
    }

    // --- setElementText ---
    @Test
    public void shouldSetElementText() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);
        XmlUtils.setElementText(elem, "hello");
        assertEquals("hello", elem.getFirstChild().getNodeValue());
    }

    @Test
    public void shouldSetElementTextToNull() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);
        elem.appendChild(doc.createTextNode("old"));
        XmlUtils.setElementText(elem, null);
        assertNull(elem.getFirstChild());
    }

    @Test
    public void shouldReplaceExistingText() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);
        elem.appendChild(doc.createTextNode("old"));
        XmlUtils.setElementText(elem, "new");
        assertEquals("new", elem.getFirstChild().getNodeValue());
    }

    @Test
    public void shouldInsertTextBeforeOtherNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);
        elem.appendChild(doc.createElement("child"));
        XmlUtils.setElementText(elem, "text");
        assertEquals(Node.TEXT_NODE, elem.getFirstChild().getNodeType());
    }

    // --- getChildElementText ---
    @Test
    public void shouldGetChildElementText() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("name");
        child.setTextContent("value");
        root.appendChild(child);
        assertEquals("value", XmlUtils.getChildElementText(root, "name"));
    }

    @Test
    public void shouldReturnNullForMissingChild() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        assertNull(XmlUtils.getChildElementText(root, "missing"));
    }

    @Test
    public void shouldReturnDefaultForMissingChild() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        assertEquals("default", XmlUtils.getChildElementText(root, "missing", "default"));
    }

    // --- getFirstChildElement ---
    @Test
    public void shouldGetFirstChildElement() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("first");
        root.appendChild(child);
        assertSame(child, XmlUtils.getFirstChildElement(root));
    }

    @Test
    public void shouldGetFirstChildElementByName() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createElement("a"));
        Element b = doc.createElement("b");
        root.appendChild(b);
        assertSame(b, XmlUtils.getFirstChildElement(root, "b"));
    }

    @Test
    public void shouldReturnNullForNullElement() {
        assertNull(XmlUtils.getFirstChildElement(null));
        assertNull(XmlUtils.getFirstChildElement(null, "name"));
    }

    // --- getFirstChildElementIgnoreCase ---
    @Test
    public void shouldGetChildElementCaseInsensitive() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("MyName");
        root.appendChild(child);
        assertSame(child, XmlUtils.getFirstChildElementIgnoreCase(root, "myname"));
    }

    @Test
    public void shouldReturnNullForNullElementIgnoreCase() {
        assertNull(XmlUtils.getFirstChildElementIgnoreCase(null, "name"));
    }

    // --- getFirstChildElementNS ---
    @Test
    public void shouldGetFirstChildElementNS() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElementNS("http://root", "r:root");
        doc.appendChild(root);
        Element child = doc.createElementNS("http://child", "c:child");
        root.appendChild(child);
        Element result = XmlUtils.getFirstChildElementNS(root, "http://child", "child");
        assertSame(child, result);
    }

    @Test
    public void shouldGetFirstChildElementNSWithNullLocalName() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElementNS("http://root", "r:root");
        doc.appendChild(root);
        Element child = doc.createElementNS("http://child", "c:child");
        root.appendChild(child);
        Element result = XmlUtils.getFirstChildElementNS(root, "http://child", null);
        assertSame(child, result);
    }

    @Test
    public void shouldGetFirstChildElementNSWithNullTns() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("child");
        root.appendChild(child);
        Element result = XmlUtils.getFirstChildElementNS(root, null, "child");
        assertSame(child, result);
    }

    @Test
    public void shouldGetFirstChildElementNSWithBothNull() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("child");
        root.appendChild(child);
        Element result = XmlUtils.getFirstChildElementNS(root, null, null);
        assertSame(child, result);
    }

    @Test
    public void shouldGetFirstChildElementNSWithEmptyTns() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("child");
        root.appendChild(child);
        Element result = XmlUtils.getFirstChildElementNS(root, "", "child");
        assertSame(child, result);
    }

    // --- getFirstChildElementNS(QName) ---
    @Test
    public void shouldGetFirstChildElementNSByQName() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElementNS("http://root", "r:root");
        doc.appendChild(root);
        Element child = doc.createElementNS("http://child", "c:child");
        root.appendChild(child);
        javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://child", "child");
        Element result = XmlUtils.getFirstChildElementNS(root, qname);
        assertSame(child, result);
    }

    // --- getElementText ---
    @Test
    public void shouldGetElementText() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        elem.setTextContent("hello");
        doc.appendChild(elem);
        assertEquals("hello", XmlUtils.getElementText(elem));
    }

    @Test
    public void shouldReturnNullWhenNoTextNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);
        assertNull(XmlUtils.getElementText(elem));
    }

    // --- getFragmentText ---
    @Test
    public void shouldGetFragmentText() throws Exception {
        Document doc = newDoc();
        DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.createTextNode("fragText"));
        assertEquals("fragText", XmlUtils.getFragmentText(frag));
    }

    @Test
    public void shouldReturnNullForEmptyFragment() throws Exception {
        Document doc = newDoc();
        DocumentFragment frag = doc.createDocumentFragment();
        assertNull(XmlUtils.getFragmentText(frag));
    }

    @Test
    public void shouldReturnNullForFragmentWithNonTextChild() throws Exception {
        Document doc = newDoc();
        DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.createElement("elem"));
        assertNull(XmlUtils.getFragmentText(frag));
    }

    // --- getNodeValue ---
    @Test
    public void shouldGetNodeValueForElement() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        elem.setTextContent("val");
        doc.appendChild(elem);
        assertEquals("val", XmlUtils.getNodeValue(elem));
    }

    @Test
    public void shouldGetNodeValueForDocumentFragment() throws Exception {
        Document doc = newDoc();
        DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.createTextNode("frag"));
        assertEquals("frag", XmlUtils.getNodeValue(frag));
    }

    @Test
    public void shouldGetNodeValueForTextNode() throws Exception {
        Document doc = newDoc();
        Text text = doc.createTextNode("textVal");
        assertEquals("textVal", XmlUtils.getNodeValue(text));
    }

    @Test
    public void shouldReturnNullForNullNode() {
        assertNull(XmlUtils.getNodeValue(null));
    }

    // --- setChildElementText ---
    @Test
    public void shouldSetChildElementText() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        XmlUtils.setChildElementText(root, "child", "val");
        assertEquals("val", XmlUtils.getChildElementText(root, "child"));
    }

    @Test
    public void shouldUpdateExistingChildElementText() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createElement("child"));
        XmlUtils.setChildElementText(root, "child", "new");
        assertEquals("new", XmlUtils.getChildElementText(root, "child"));
    }

    // --- addChildElement ---
    @Test
    public void shouldAddChildElementWithText() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = XmlUtils.addChildElement(root, "child", "text");
        assertEquals("child", child.getNodeName());
        assertEquals("text", child.getTextContent());
    }

    @Test
    public void shouldAddChildElementWithoutText() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = XmlUtils.addChildElement(root, "child", null);
        assertEquals("child", child.getNodeName());
    }

    // --- parseXml ---
    @Test
    public void shouldParseXmlString() throws Exception {
        Document doc = XmlUtils.parseXml("<root/>");
        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getNodeName());
    }

    // --- createDefaultXmlOptions ---
    @Test
    public void shouldCreateDefaultXmlOptions() {
        assertNotNull(XmlUtils.createDefaultXmlOptions());
    }

    // --- seemsToBeXml ---
    @Test
    public void shouldDetectValidXml() {
        assertTrue(XmlUtils.seemsToBeXml("<root/>"));
    }

    @Test
    public void shouldDetectInvalidXml() {
        assertFalse(XmlUtils.seemsToBeXml("not xml"));
    }

    @Test
    public void shouldReturnFalseForBlankString() {
        assertFalse(XmlUtils.seemsToBeXml(""));
        assertFalse(XmlUtils.seemsToBeXml(null));
    }

    // --- getQName ---
    @Test
    public void shouldGetQNameFromNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElementNS("http://ns", "local");
        doc.appendChild(elem);
        javax.xml.namespace.QName qname = XmlUtils.getQName(elem);
        assertNotNull(qname);
        assertEquals("http://ns", qname.getNamespaceURI());
        assertEquals("local", qname.getLocalPart());
    }

    @Test
    public void shouldReturnNullQNameForNullNode() {
        assertNull(XmlUtils.getQName((Node) null));
    }

    @Test
    public void shouldGetQNameFromNodeWithoutNamespace() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("local");
        doc.appendChild(elem);
        javax.xml.namespace.QName qname = XmlUtils.getQName(elem);
        assertNotNull(qname);
    }

    @Test
    public void shouldGetQNameFromXmlObject() throws Exception {
        XmlObject xml = XmlUtils.createXmlObject("<root/>");
        javax.xml.namespace.QName qname = XmlUtils.getQName(xml);
        assertNotNull(qname);
    }

    @Test
    public void shouldReturnNullQNameForNullXmlObject() {
        assertNull(XmlUtils.getQName((XmlObject) null));
    }

    // --- createQName ---
    @Test
    public void shouldCreateQNameFromNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElementNS("http://ns", "prefix:local");
        doc.appendChild(elem);
        javax.xml.namespace.QName qname = XmlUtils.createQName(elem);
        assertEquals("http://ns", qname.getNamespaceURI());
        assertEquals("local", qname.getLocalPart());
    }

    // --- getElementPath ---
    @Test
    public void shouldGetElementPath() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element child = doc.createElement("child");
        root.appendChild(child);
        String path = XmlUtils.getElementPath(child);
        assertNotNull(path);
        assertTrue(path.contains("root"));
        assertTrue(path.contains("child"));
    }

    // --- getElementIndex ---
    @Test
    public void shouldGetElementIndex() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element c1 = doc.createElement("item");
        Element c2 = doc.createElement("item");
        root.appendChild(c1);
        root.appendChild(c2);
        assertEquals(1, XmlUtils.getElementIndex(c1));
        assertEquals(2, XmlUtils.getElementIndex(c2));
    }

    // --- setNodeValue ---
    @Test
    public void shouldSetNodeValueOnElement() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        doc.appendChild(elem);
        assertTrue(XmlUtils.setNodeValue(elem, "value"));
        assertEquals("value", elem.getTextContent());
    }

    @Test
    public void shouldSetNodeValueOnAttribute() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Attr attr = doc.createAttribute("attr");
        attr.setValue("old");
        root.setAttributeNode(attr);
        assertTrue(XmlUtils.setNodeValue(attr, "new"));
        assertEquals("new", attr.getValue());
    }

    @Test
    public void shouldSetNodeValueOnText() throws Exception {
        Document doc = newDoc();
        Text text = doc.createTextNode("old");
        assertTrue(XmlUtils.setNodeValue(text, "new"));
        assertEquals("new", text.getNodeValue());
    }

    @Test
    public void shouldSetNodeValueOnProcessingInstruction() throws Exception {
        Document doc = newDoc();
        ProcessingInstruction pi = doc.createProcessingInstruction("target", "data");
        assertTrue(XmlUtils.setNodeValue(pi, "newData"));
    }

    @Test
    public void shouldSetNodeValueOnCDATA() throws Exception {
        Document doc = newDoc();
        CDATASection cdata = doc.createCDATASection("old");
        assertTrue(XmlUtils.setNodeValue(cdata, "new"));
    }

    @Test
    public void shouldReturnFalseForNullNode() {
        assertFalse(XmlUtils.setNodeValue(null, "value"));
    }

    // --- getNextElementSibling ---
    @Test
    public void shouldGetNextElementSibling() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element c1 = doc.createElement("first");
        root.appendChild(c1);
        root.appendChild(doc.createTextNode("text"));
        Element c2 = doc.createElement("second");
        root.appendChild(c2);
        Node next = XmlUtils.getNextElementSibling(c1);
        assertSame(c2, next);
    }

    @Test
    public void shouldReturnNullWhenNoNextElementSibling() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element c1 = doc.createElement("only");
        root.appendChild(c1);
        assertNull(XmlUtils.getNextElementSibling(c1));
    }

    // --- createDocument ---
    @Test
    public void shouldCreateDocumentWithQName() {
        javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://ns", "root");
        Document doc = XmlUtils.createDocument(qname);
        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getLocalName());
    }

    // --- getChildElements ---
    @Test
    public void shouldGetChildElements() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createElement("a"));
        root.appendChild(doc.createTextNode("text"));
        root.appendChild(doc.createElement("b"));
        NodeList children = XmlUtils.getChildElements(root);
        assertEquals(2, children.getLength());
    }

    // --- getChildElementsByTagName ---
    @Test
    public void shouldGetChildElementsByTagName() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.appendChild(doc.createElement("item"));
        root.appendChild(doc.createElement("other"));
        root.appendChild(doc.createElement("item"));
        NodeList items = XmlUtils.getChildElementsByTagName(root, "item");
        assertEquals(2, items.getLength());
    }

    // --- getChildElementsNS ---
    @Test
    public void shouldGetChildElementsNS() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElementNS("http://root", "r:root");
        doc.appendChild(root);
        root.appendChild(doc.createElementNS("http://child", "c:a"));
        root.appendChild(doc.createElementNS("http://child", "c:b"));
        javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://child", "a");
        NodeList result = XmlUtils.getChildElementsNS(root, qname);
        assertEquals(1, result.getLength());
    }

    // --- getChildElementsByTagNameNS ---
    @Test
    public void shouldGetChildElementsByTagNameNS() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElementNS("http://root", "r:root");
        doc.appendChild(root);
        root.appendChild(doc.createElementNS("http://child", "c:a"));
        root.appendChild(doc.createElementNS("http://child", "c:b"));
        root.appendChild(doc.createElementNS("http://child", "c:a"));
        NodeList result = XmlUtils.getChildElementsByTagNameNS(root, "http://child", "a");
        assertEquals(2, result.getLength());
    }

    // --- prettyPrintXml(String) ---
    @Test
    public void shouldPrettyPrintXmlString() {
        String xml = "<root><child/></root>";
        String result = XmlUtils.prettyPrintXml(xml);
        assertNotNull(result);
        assertTrue(result.contains("root"));
    }

    @Test
    public void shouldReturnNonXmlAsIs() {
        assertEquals("not xml", XmlUtils.prettyPrintXml("not xml"));
    }

    // --- prettyPrintXml(XmlObject) ---
    @Test
    public void shouldPrettyPrintXmlObject() throws Exception {
        XmlObject xml = XmlUtils.createXmlObject("<root><child/></root>");
        String result = XmlUtils.prettyPrintXml(xml);
        assertNotNull(result);
        assertTrue(result.contains("root"));
    }

    @Test
    public void shouldReturnNullForNullXmlObject() {
        assertNull(XmlUtils.prettyPrintXml((XmlObject) null));
    }

    // --- removeXPathNamespaceDeclarations ---
    @Test
    public void shouldRemoveXPathNamespaceDeclarations() {
        String xpath = "declare namespace ns='http://ns'; /ns:root/ns:child";
        String result = XmlUtils.removeXPathNamespaceDeclarations(xpath);
        assertEquals("/ns:root/ns:child", result);
    }

    @Test
    public void shouldReturnXPathUnchangedWithoutDeclarations() {
        String xpath = "/root/child";
        assertEquals("/root/child", XmlUtils.removeXPathNamespaceDeclarations(xpath));
    }

    // --- transferValues ---
    @Test
    public void shouldTransferValuesBetweenXml() {
        String source = "<root><child>val</child></root>";
        String dest = "<root><child/></root>";
        String result = XmlUtils.transferValues(source, dest);
        assertNotNull(result);
    }

    @Test
    public void shouldReturnDestWhenSourceIsBlank() {
        String dest = "<root><child/></root>";
        assertEquals(dest, XmlUtils.transferValues("", dest));
    }

    // --- extractNamespaces ---
    @Test
    public void shouldExtractNamespacesFromXPath() {
        String xpath = "declare namespace ns='http://ns'; /ns:root";
        String result = XmlUtils.extractNamespaces(xpath);
        assertTrue(result.contains("declare namespace"));
    }

    @Test
    public void shouldReturnEmptyForNoDeclarations() {
        String xpath = "/root/child";
        assertEquals("", XmlUtils.extractNamespaces(xpath));
    }

    // --- replaceNameInPathOrQuery ---
    @Test
    public void shouldReplaceNameInPath() throws Exception {
        String result = XmlUtils.replaceNameInPathOrQuery("/old/path", "old", "new");
        assertEquals("/new/path", result);
    }

    @Test
    public void shouldNotReplaceUnmatchedName() throws Exception {
        String result = XmlUtils.replaceNameInPathOrQuery("/root/path", "other", "new");
        assertEquals("/root/path", result);
    }

    // --- getValueForMatch(Node, boolean) ---
    @Test
    public void shouldGetValueForMatchFromNode() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("test");
        elem.setTextContent("value");
        doc.appendChild(elem);
        String result = XmlUtils.getValueForMatch(elem, true);
        assertEquals("value", result);
    }

    @Test
    public void shouldGetValueForMatchFromAttribute() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Attr attr = doc.createAttribute("attr");
        attr.setValue("val");
        root.setAttributeNode(attr);
        String result = XmlUtils.getValueForMatch(attr, false);
        assertEquals("val", result);
    }

    @Test
    public void shouldGetValueForMatchFromTextNode() throws Exception {
        Document doc = newDoc();
        Text text = doc.createTextNode("textVal");
        String result = XmlUtils.getValueForMatch(text, false);
        assertEquals("textVal", result);
    }

    // --- getValueForMatch from element with child elements ---

    // --- hasContentAttributes ---
    @Test
    public void shouldDetectContentAttributes() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("root");
        doc.appendChild(elem);
        elem.setAttributeNS("http://custom", "custom:attr", "val");
        assertTrue(XmlUtils.hasContentAttributes(elem));
    }

    @Test
    public void shouldNotDetectXmlnsAsContent() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElementNS("http://ns", "root");
        doc.appendChild(elem);
        assertFalse(XmlUtils.hasContentAttributes(elem));
    }

    // --- removeUnneccessaryNamespaces ---
    @Test
    public void shouldRemoveUnnecessaryNamespaces() {
        String xml = "<root xmlns:unused=\"http://unused\"><child/></root>";
        String result = XmlUtils.removeUnneccessaryNamespaces(xml);
        assertNotNull(result);
    }

    @Test
    public void shouldReturnNullForNullInput() {
        assertNull(XmlUtils.removeUnneccessaryNamespaces(null));
    }

    @Test
    public void shouldReturnBlankAsIs() {
        assertEquals("", XmlUtils.removeUnneccessaryNamespaces(""));
    }

    // --- findTypeNameForXsiType ---
    @Test
    public void shouldFindTypeNameForXsiType() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.setAttribute("xmlns:ns", "http://ns");
        javax.xml.namespace.QName result = XmlUtils.findTypeNameForXsiType("ns:TypeName", root);
        assertNotNull(result);
        assertEquals("http://ns", result.getNamespaceURI());
        assertEquals("TypeName", result.getLocalPart());
    }

    @Test
    public void shouldReturnNullForNoColonInTypeName() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        assertNull(XmlUtils.findTypeNameForXsiType("NoColon", root));
    }

    @Test
    public void shouldReturnNullForEmptyNamespaceUri() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        assertNull(XmlUtils.findTypeNameForXsiType("unknown:Type", root));
    }

    // --- findPrefixForNamespace ---
    @Test
    public void shouldFindPrefixForNamespace() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.setAttribute("xmlns:my", "http://my.ns");
        assertEquals("my", XmlUtils.findPrefixForNamespace(root, "http://my.ns"));
    }

    @Test
    public void shouldReturnNullWhenPrefixNotFound() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        assertNull(XmlUtils.findPrefixForNamespace(root, "http://nonexistent"));
    }

    // --- setXsiType ---
    @Test
    public void shouldSetXsiType() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.setAttribute("xmlns:ns", "http://ns");
        javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://ns", "MyType");
        XmlUtils.setXsiType(root, qname);
        String typeAttr = root.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
        assertNotNull(typeAttr);
        assertTrue(typeAttr.contains("MyType"));
    }

    @Test
    public void shouldSetXsiTypeWithNewPrefix() throws Exception {
        Document doc = newDoc();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://new.ns", "NewType");
        XmlUtils.setXsiType(root, qname);
        String typeAttr = root.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
        assertNotNull(typeAttr);
        assertTrue(typeAttr.contains("NewType"));
    }

    // --- createXPathData (avoid triggering SoapVersion static init) ---

    // --- createXPath variants (avoid triggering SoapVersion static init) ---

    // --- setXPathContent (avoid triggering SoapVersion/commons-cli init) ---

    // --- getValueForMatch(XmlCursor) ---

    // --- stripWhitespaces ---
    @Test
    public void shouldStripWhitespaces() {
        String xml = "<root><child/></root>";
        String result = XmlUtils.stripWhitespaces(xml);
        assertNotNull(result);
    }

    // --- declareXPathNamespaces (avoid xpath engine init) ---

    // --- getQName with null namespace ---
    @Test
    public void shouldGetQNameWithNullNamespace() throws Exception {
        Document doc = newDoc();
        Element elem = doc.createElement("local");
        doc.appendChild(elem);
        javax.xml.namespace.QName qname = XmlUtils.getQName(elem);
        assertNotNull(qname);
    }

    private Document newDoc() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.newDocument();
    }
}
