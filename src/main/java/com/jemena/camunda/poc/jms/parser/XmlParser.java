package com.jemena.camunda.poc.jms.parser;

import com.jemena.camunda.poc.jms.model.InboundMessage;

public class XmlParser {

    public static InboundMessage parse(String xml) {
        try {
            return ServiceOrderXmlExtractor.extract(xml);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JMS XML", e);
        }
    }
}