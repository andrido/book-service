package com.exadel.bookService.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka.topics")
public class KafkaTopicsProperties {
    private String bookEvents;
    private String loanEvents;

    @PostConstruct
    public void debug() {
        System.out.println("bookEvents = " + bookEvents);
        System.out.println("loanEvents = " + loanEvents);
    }
}

