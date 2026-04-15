package com.jemena.camunda.poc.service;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
@Slf4j
public class CamundaIntegrationService {

    private final CamundaClient camundaClient;
    private final String processId;

    public CamundaIntegrationService(CamundaClient camundaClient,
                                     @Value("${jemena.msi.process.id}") String processId) {
        this.camundaClient = camundaClient;
        this.processId = processId;
    }

    public void startProcess(Map<String, Object> vars, String payload) {

        Object txId = (vars != null) ? vars.get("transactionId") : null;
        log.info("Starting Camunda process for transaction id: {}", txId);
        //log.info("Process variables Jignesh: {}", vars);
        // use payload to avoid unused parameter warnings in static analysis
        log.debug("startProcess payload length={}", payload != null ? payload.length() : 0);

        log.info("Starting process with BPMN id: {}", processId);

        ProcessInstanceEvent pid = camundaClient
                .newCreateInstanceCommand()
                .bpmnProcessId(processId)
                .latestVersion()
                .variables(vars)
                .send()
                .join();

        log.info("Started process instance with id: {}", pid.getProcessInstanceKey());


    }
}
