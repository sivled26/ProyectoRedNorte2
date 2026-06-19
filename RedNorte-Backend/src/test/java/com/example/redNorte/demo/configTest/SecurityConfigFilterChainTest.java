package com.example.redNorte.demo.configTest;

import com.example.redNorte.demo.config.SecurityConfig;
import com.example.redNorte.demo.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigFilterChainTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private JwtService jwtService;

    @Test
    void filterChain_debeEstarConfigurado() {
        assertNotNull(securityFilterChain);
    }

    @Test
    void securityConfig_debeEstarCargado() {
        assertNotNull(securityConfig);
        assertNotNull(jwtService);
    }
}
