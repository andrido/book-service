package com.exadel.bookService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookTopic() {
        return new NewTopic("book-topic", 1, (short) 1);
    }
}
