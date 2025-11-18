package com.exadel.bookService.kafka;

import com.exadel.bookService.config.KafkaTopicsProperties;
import com.exadel.bookService.dto.BookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void sendBookEvent(BookEvent event) {
        kafkaTemplate.send(topics.getBookEvents(), event);
        System.out.println("📤 Book event sent: " + event);
    }
}