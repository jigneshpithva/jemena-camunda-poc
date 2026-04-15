package com.jemena.camunda.poc.jms.outbound;


import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsProducerService {

    private static final Logger LOG =
            LoggerFactory.getLogger(JmsProducerService.class);

    private final JmsTemplate jmsTemplate;

    public JmsProducerService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(String destination, String payload, String correlationId) {

        jmsTemplate.send(destination, session -> {
            try {
                TextMessage message =
                        session.createTextMessage(payload);

                if (correlationId != null) {
                    message.setJMSCorrelationID(correlationId);
                }

                return message;

            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to create JMS message", e
                );
            }
        });

        LOG.info(
                "JMS message published. destination={}, correlationId={}",
                destination, correlationId
        );
    }
}


