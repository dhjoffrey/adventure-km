package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.LoginRequest;
import com.adventurekm.backend.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginWithSeedUser() {
        AuthResponse response = authService.login(new LoginRequest("joffrey", "123456"));
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }
}
