package com.jemena.camunda.poc.jms.inbound;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${broker.url}") String brokerUrl,
            @Value("${broker.username}") String username,
            @Value("${broker.password}") String password
    ) {
        return new org.apache.activemq.artemis.jms.client
                .ActiveMQConnectionFactory(brokerUrl, username, password);
    }
}

