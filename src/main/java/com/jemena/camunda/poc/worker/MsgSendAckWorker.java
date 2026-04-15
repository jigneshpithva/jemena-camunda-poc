package com.jemena.camunda.poc.worker;

import com.jemena.camunda.poc.jms.outbound.JmsProducerService;
import com.jemena.camunda.poc.util.XmlUtils;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
@Component
public class MsgSendAckWorker {

    private static final Logger log = LoggerFactory.getLogger(MsgSendAckWorker.class);
    private static final String ACK_SENT = "ackSent";

    private final JmsProducerService jmsProducerService;

    public MsgSendAckWorker(JmsProducerService jmsProducerService) {
        this.jmsProducerService = jmsProducerService;
    }

    @JobWorker(type = "msg-send-ack", autoComplete = false)
    public void handle(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Map<String, Object> result = new HashMap<>();

        String txId = safe(vars.get("transactionId"));
        // if transactionId not provided by Camunda, generate a random numeric id
        if (txId == null || txId.isEmpty()) {
            txId = String.valueOf(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
            log.info("[msg-send-ack] transactionId missing from variables; generated txId='{}'", txId);
        }
        // Always ensure we send transactionId back to Camunda so it gets persisted/updated
        result.put("transactionId", txId);

        String sourceSystem = safe(vars.get("sourceSystem"));
        String targetSystem = safe(vars.get("targetSystem"));
        String transactionStatus = safe(vars.get("transactionStatus"));
        String duplicate = safe(vars.get("duplicate"));
        String transactionGroup = safe(vars.get("transactionGroup"));
        String destinationQueue = safe(vars.get("destinationQueue"));

        log.info("[msg-send-ack] Preparing ACK for transactionId='{}' destination='{}' status='{}' duplicate='{}'",
                txId, destinationQueue, transactionStatus, duplicate);


        if (destinationQueue == null || destinationQueue.isEmpty()) {
            String msg = "Missing required Camunda variable 'destinationQueue'";
            log.error("[msg-send-ack] {}", msg);
            result.put(ACK_SENT, false);
            result.put("ackMessage", msg);
            completeJob(client, job.getKey(), result, txId);
            return;
        }

        // Load TransAck template
        String templatePath = "/messages/TransAck.xml";
        String xmlTemplate;
        try (InputStream is = this.getClass().getResourceAsStream(templatePath)) {
            if (is == null) {
                String m = "ACK template not found on classpath: " + templatePath;
                log.error("[msg-send-ack] {}", m);
                result.put(ACK_SENT, false);
                result.put("ackMessage", m);
                completeJob(client, job.getKey(), result, txId);
                return;
            }
            xmlTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            String m = "Failed to read ACK template: " + e.getMessage();
            log.error("[msg-send-ack] {}", m, e);
            result.put(ACK_SENT, false);
            result.put("ackMessage", m);
            completeJob(client, job.getKey(), result, txId);
            return;
        }

        try {
            // set initiatingTransactionID attribute on TransactionAcknowledgement
            String finalXml = XmlUtils.replaceAttribute(xmlTemplate, "TransactionAcknowledgement", "initiatingTransactionID", txId);

            // set status and duplicate attributes on TransactionAcknowledgement
            finalXml = XmlUtils.replaceAttribute(finalXml, "TransactionAcknowledgement", "status", transactionStatus);
            finalXml = XmlUtils.replaceAttribute(finalXml, "TransactionAcknowledgement", "duplicate", duplicate);

            // If transactionGroup variable provided, update TransactionGroup element text
            if (transactionGroup != null && !transactionGroup.isEmpty()) {
                finalXml = XmlUtils.replaceElementText(finalXml, "TransactionGroup", transactionGroup);
            }

            // set From and To elements in Envelope
            finalXml = XmlUtils.replaceElementText(finalXml, "From", sourceSystem);
            finalXml = XmlUtils.replaceElementText(finalXml, "To", targetSystem);

            log.info("[msg-send-ack] Publishing ACK to JMS destination='{}' correlationId='{}'. ack xml: '{}'",
                    destinationQueue, txId, finalXml);
            jmsProducerService.send(destinationQueue, finalXml, txId);

            result.put(ACK_SENT, true);
            result.put("ackMessage", "Published ACK to " + destinationQueue);
            result.put("ackPayload", finalXml);
            completeJob(client, job.getKey(), result, txId);

        } catch (Exception e) {
            String m = "Failed to prepare or publish ACK: " + e.getMessage();
            log.error("[msg-send-ack] {}", m, e);
            result.put(ACK_SENT, false);
            result.put("ackMessage", m);
            completeJob(client, job.getKey(), result, txId);
        }
    }

    private void completeJob(JobClient client, long jobKey, Map<String, Object> vars, String txId) {
        try {
            log.info("[msg-send-ack] Completing job '{}' for transactionId='{}'.", jobKey, txId);
            client.newCompleteCommand(jobKey).variables(vars).send().join();
            log.info("[msg-send-ack] Completed job '{}' successfully", jobKey);
        } catch (Exception e) {
            log.error("[msg-send-ack] Failed to complete job '{}' with vars {}. cause={}", jobKey, vars, e.toString());
            throw e;
        }
    }

    private static String safe(Object o) {
        return o == null ? "" : o.toString();
    }

}
