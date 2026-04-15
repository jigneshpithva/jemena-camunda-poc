package com.jemena.camunda.poc.jms.parser;

import com.jemena.camunda.poc.jms.model.*;

import com.jemena.camunda.poc.jms.model.ServiceOrderMessage;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class ServiceOrderXmlExtractor {

    public static ServiceOrderMessage extract(String xml) throws Exception {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));

        ServiceOrderMessage msg = new ServiceOrderMessage();
        msg.setRawXml(xml);

        // 1️⃣ Detect message type
        detectMessageType(document, msg);

        // 2️⃣ Extract transaction IDs
        extractTransactionIds(document, msg);

        // 3️⃣ Extract business data
        extractBusinessData(document, msg);

        // 4️⃣ Extract source / target
        extractSystems(document, msg);

        return msg;
    }

    private static void detectMessageType(
            Document doc,
            ServiceOrderMessage msg
    ) {
        msg.setMessageType("REQUEST");
        //msg.setTransactionType("ServiceOrderRequest");
    }

    private static void extractTransactionIds(
            Document doc,
            ServiceOrderMessage msg
    ) {
        NodeList txNodes =
                doc.getElementsByTagNameNS("*", "Transaction");

        if (txNodes.getLength() == 0) return;

        Element tx = (Element) txNodes.item(0);

        msg.setTransactionId(tx.getAttribute("marketTransactionId"));
        msg.setInitiatingTransactionId(
                tx.getAttribute("marketInitiatingTransactionId")
        );
        msg.setTransactionType(tx.getAttribute("transactionType"));
    }

    private static void extractBusinessData(
            Document doc,
            ServiceOrderMessage msg
    ) {
        // Service Order Number
        NodeList soNumbers =
                doc.getElementsByTagNameNS("*", "ServiceOrderNumber");
        if (soNumbers.getLength() > 0) {
            msg.setServiceOrderNumber(
                    soNumbers.item(0).getTextContent()
            );
        }

        // NMI
        NodeList nmiNodes =
                doc.getElementsByTagNameNS("*", "NMI");
        if (nmiNodes.getLength() > 0) {
            msg.setNmi(nmiNodes.item(0).getTextContent());
        }

        // Work Type
        NodeList workTypes =
                doc.getElementsByTagNameNS("*", "WorkType");
        if (workTypes.getLength() > 0) {
            msg.setWorkType(
                    workTypes.item(0).getTextContent()
            );
        }

        /*// FROM (for REQUEST)
        NodeList fromNodes =
                doc.getElementsByTagNameNS("*", "From");
        if (fromNodes.getLength() > 0) {
            msg.setFrom(fromNodes.item(0).getTextContent());
        }*/

        //Schedule Date (for REQUEST)
        NodeList scheduleNodes =
                doc.getElementsByTagNameNS("*", "ScheduledDate");
        if (scheduleNodes.getLength() > 0) {
            msg.setScheduledDate(
                    scheduleNodes.item(0).getTextContent()
            );
        }

        // Response Status (for RESPONSE)
        NodeList statusNodes =
                doc.getElementsByTagNameNS("*", "ServiceOrderStatus");
        if (statusNodes.getLength() > 0) {
            msg.setResponseStatus(
                    statusNodes.item(0).getTextContent()
            );
        }
    }

    private static void extractSystems(
            Document doc,
            ServiceOrderMessage msg
    ) {
        NodeList fromNodes = doc.getElementsByTagNameNS("*", "From");
        if (fromNodes.getLength() > 0) {
            msg.setSourceSystem(fromNodes.item(0).getTextContent());
        }

        NodeList toNodes = doc.getElementsByTagNameNS("*", "To");
        if (toNodes.getLength() > 0) {
            msg.setTargetSystem(toNodes.item(0).getTextContent());
        }
    }
}



