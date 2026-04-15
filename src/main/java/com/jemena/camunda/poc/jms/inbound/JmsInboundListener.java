package com.jemena.camunda.poc.jms.inbound;

import com.jemena.camunda.poc.jms.mapper.ServiceOrderVariableMapper;
import com.jemena.camunda.poc.jms.model.InboundMessage;
import com.jemena.camunda.poc.jms.model.ServiceOrderMessage;
import com.jemena.camunda.poc.jms.parser.XmlParser;
import com.jemena.camunda.poc.service.CamundaIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JmsInboundListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(JmsInboundListener.class);

    private final CamundaIntegrationService camundaService;

    public JmsInboundListener(CamundaIntegrationService camundaService) {
        this.camundaService = camundaService;
    }

    @JmsListener(destination = "${jemena.jms.queues.inbound}")
    public void onMessage(String xml) {

        try {
            InboundMessage parsed = XmlParser.parse(xml);

            Map<String, Object> variables;

            variables = ServiceOrderVariableMapper.toVariables((ServiceOrderMessage) parsed);

            LOG.info("Received JMS message. variables={}", variables);

            camundaService.startProcess(variables, xml);

        } catch (Exception e) {
            LOG.error("Failed to process inbound JMS message", e);
            throw e; // important for retry / DLQ
        }
    }
}
