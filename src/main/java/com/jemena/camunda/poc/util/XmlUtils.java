package com.jemena.camunda.poc.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Small helper utilities for simple DOM-based XML edits used by workers.
 * Keeps DOM operations in one place to reduce duplication.
 */
public final class XmlUtils {

    private XmlUtils() { }

    public static String replaceElementText(String xml, String localName, String newValue) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            var doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

            NodeList nodes = doc.getElementsByTagNameNS("*", localName);
            if (nodes.getLength() == 0) return xml;

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                if (newValue == null) el.setTextContent("");
                else el.setTextContent(newValue);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();

        } catch (Exception e) {
            // Best-effort: if we fail to modify the XML, return the original so callers can handle it.
            return xml;
        }
    }

    public static String replaceAttribute(String xml, String elementLocalName, String attrName, String newValue) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            var doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

            NodeList nodes = doc.getElementsByTagNameNS("*", elementLocalName);
            if (nodes.getLength() == 0) return xml;

            Element el = (Element) nodes.item(0);
            if (el != null) {
                if (newValue == null) el.removeAttribute(attrName);
                else el.setAttribute(attrName, newValue);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();

        } catch (Exception e) {
            return xml;
        }
    }
}

