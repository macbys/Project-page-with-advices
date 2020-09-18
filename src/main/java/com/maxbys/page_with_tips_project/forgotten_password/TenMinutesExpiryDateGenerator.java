package com.maxbys.page_with_tips_project.forgotten_password;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TenMinutesExpiryDateGenerator {

    public LocalDateTime createTenMinutesExpiryDate() {
        return LocalDateTime.now().plusMinutes(10);
    }
}
