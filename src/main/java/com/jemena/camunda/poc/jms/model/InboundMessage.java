package com.jemena.camunda.poc.jms.model;

public interface InboundMessage {
    String getMessageType();
    String getTransactionId();
    String getRawXml();
}

