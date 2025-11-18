package com.exadel.bookService.client;

import com.exadel.bookService.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserResponse getUserById(Long userId) {
        return restTemplate.getForObject(userServiceUrl + "/users/" + userId, UserResponse.class);
    }
}