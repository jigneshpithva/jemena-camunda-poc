package com.jemena.camunda.poc.jms.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceOrderMessage implements InboundMessage {

    private String messageType;

    private String transactionId;
    private String initiatingTransactionId;

    private String serviceOrderNumber;
    private String nmi;
    private String from;

    private String transactionType;
    private String workType;
    private String actionType;
    private String responseStatus;

    private String sourceSystem;
    private String targetSystem;

    private String scheduledDate;
    private String actualDateTime;

    private String rawXml;

    @Override
    public String toString() {
        return "ServiceOrderMessage{" +
                "messageType=" + messageType +
                ", transactionId='" + transactionId + '\'' +
                ", initiatingTransactionId='" + initiatingTransactionId + '\'' +
                ", serviceOrderNumber='" + serviceOrderNumber + '\'' +
                ", nmi='" + nmi + '\'' +
                ", from='" + from + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", workType='" + workType + '\'' +
                ", actionType='" + actionType + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", targetSystem='" + targetSystem + '\'' +
                ", scheduledDate='" + scheduledDate + '\'' +
                ", actualDateTime='" + actualDateTime + '\'' +
                '}';
    }
}