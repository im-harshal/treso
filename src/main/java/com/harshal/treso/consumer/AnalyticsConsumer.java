package com.harshal.treso.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshal.treso.event.ExpenseCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "treso.expenses.events", groupId = "analytics-group")
    public void consume(String message) {
        try {
            ExpenseCreatedEvent event = objectMapper.readValue(message, ExpenseCreatedEvent.class);
            System.out.println("=========================================");
            System.out.println("🚀 KAFKA EVENT RECEIVED IN ANALYTICS SERVICE");
            System.out.println("User ID: " + event.getUserId() + " spent $" + event.getAmount() + " on " + event.getCategory());
            System.out.println("Here is where we would update a Redis cache for dashboard totals!");
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Failed to process Kafka message: " + e.getMessage());
        }
    }
}
