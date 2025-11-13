package com.exadel.bookService.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "loan-events";

    public void sendLoanEvent(Object event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
