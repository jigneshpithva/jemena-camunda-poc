package com.jemena.camunda.poc.jms.mapper;

import com.jemena.camunda.poc.jms.model.ServiceOrderMessage;

import java.util.HashMap;
import java.util.Map;

public class ServiceOrderVariableMapper {

    public static Map<String, Object> toVariables(
            ServiceOrderMessage msg
    ) {
        Map<String, Object> vars = new HashMap<>();

        vars.put("messageType", msg.getMessageType());
        vars.put("transactionId", msg.getTransactionId());
        vars.put("initiatingTransactionId", msg.getInitiatingTransactionId());
        vars.put("transactionType", msg.getTransactionType());

        vars.put("serviceOrderNumber", msg.getServiceOrderNumber());
        vars.put("nmi", msg.getNmi());
        vars.put("workType", msg.getWorkType());
        vars.put("scheduledDate", msg.getScheduledDate());

        // For LifeSupportNotification we also expose actualDateTime (LastModifiedDateTime)
        vars.put("actualDateTime", msg.getActualDateTime());

        vars.put("responseStatus", msg.getResponseStatus());

        vars.put("sourceSystem", msg.getSourceSystem());
        vars.put("targetSystem", msg.getTargetSystem());

        // ⚠️ POC only – remove later if not needed
        vars.put("transactionReq", msg.getRawXml());

        return vars;
    }
}