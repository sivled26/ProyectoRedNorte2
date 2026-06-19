package com.example.redNorte.demo.serviceTest;

import com.example.redNorte.demo.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_debeGenerarTokenValido() {
        String token = jwtService.generateToken("usuario@test.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractCorreo_debeRetornarCorreoDelToken() {
        String correo = "paciente@test.com";
        String token = jwtService.generateToken(correo);

        assertEquals(correo, jwtService.extractCorreo(token));
    }

    @Test
    void extractCorreo_conTokenInvalido_debeLanzarExcepcion() {
        assertThrows(Exception.class, () -> jwtService.extractCorreo("token-invalido"));
    }
}
