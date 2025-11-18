package com.exadel.bookService.kafka;

import com.exadel.bookService.config.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void sendLoanEvent(Object event) {
        kafkaTemplate.send(topics.getLoanEvents(), event);
        System.out.println("📤 Loan event sent: " + event);
    }
}
