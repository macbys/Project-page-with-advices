package com.maxbys.page_with_tips_project.forgotten_password;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RandomStringGenerator {

    public String generateRandomUuid() {
        return UUID.randomUUID().toString();
    }
}
