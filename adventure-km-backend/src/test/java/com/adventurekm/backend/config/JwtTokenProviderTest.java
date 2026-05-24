package com.adventurekm.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setSecret("test-secret-key-that-must-be-at-least-256-bits-long-for-hs256-algorithm");
        jwtTokenProvider.setAccessTokenExpirationMs(900_000L);
        jwtTokenProvider.setRefreshTokenExpirationMs(2_592_000_000L);
        jwtTokenProvider.init();
    }

    @Test
    void generateAndValidateAccessToken() {
        String token = jwtTokenProvider.generateAccessToken("joffrey", "USER");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("joffrey");
    }

    @Test
    void generateAndValidateRefreshToken() {
        String token = jwtTokenProvider.generateRefreshToken("joffrey", "USER");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("joffrey");
    }

    @Test
    void expiredTokenIsInvalid() {
        jwtTokenProvider.setAccessTokenExpirationMs(0L);
        jwtTokenProvider.init();
        String token = jwtTokenProvider.generateAccessToken("joffrey", "USER");
        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    }

    @Test
    void corruptedTokenIsInvalid() {
        assertThat(jwtTokenProvider.validateToken("garbage.token.value")).isFalse();
    }
}
