package com.exadel.bookService.kafka;

import com.exadel.bookService.dto.BookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "book-events";

    public void sendBookEvent(BookEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("📤 Enviado evento de livro: " + event);
    }
}
