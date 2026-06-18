package com.example.redNorte.demo.configTest;

import com.example.redNorte.demo.config.SecurityConfig;
import com.example.redNorte.demo.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final SecurityConfig securityConfig = new SecurityConfig(jwtService);

    @Test
    void passwordEncoder_debeRetornarBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void passwordEncoder_debeCodificarContrasena() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String raw = "miContrasena123";

        String encoded = encoder.encode(raw);

        assertNotNull(encoded);
        assertNotEquals(raw, encoded);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    void passwordEncoder_dosHashesDeMismaContrasenaDebenSerDistintos() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String raw = "miContrasena123";

        String hash1 = encoder.encode(raw);
        String hash2 = encoder.encode(raw);

        // BCrypt genera salt aleatorio, nunca deben ser iguales
        assertNotEquals(hash1, hash2);
    }
}