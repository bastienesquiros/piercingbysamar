package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-piercing-by-samar-must-be-32-chars!!");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000L);

        userDetails = new User("admin@test.com", "hashed", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void generateToken_shouldReturnNonEmptyToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@test.com");
    }

    @Test
    void isValid_withValidTokenAndMatchingUser_shouldReturnTrue() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isValid(token, userDetails)).isTrue();
    }

    @Test
    void isValid_withValidTokenButDifferentUser_shouldReturnFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("other@test.com", "hashed", List.of());
        assertThat(jwtService.isValid(token, otherUser)).isFalse();
    }

    @Test
    void isValid_withExpiredToken_shouldReturnFalse() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L); // déjà expiré
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isValid(token, userDetails)).isFalse();
    }
}
