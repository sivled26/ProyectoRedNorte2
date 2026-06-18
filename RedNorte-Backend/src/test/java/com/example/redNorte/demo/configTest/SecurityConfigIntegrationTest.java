package com.example.redNorte.demo.configTest;

import com.example.redNorte.demo.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void registroEndpoint_debeSerPublico() throws Exception {
        mockMvc.perform(post("/api/pacientes/registro")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk()); // o 4xx según tu lógica, pero NO 401/403
    }

    @Test
    void loginEndpoint_debeSerPublico() throws Exception {
        mockMvc.perform(post("/api/pacientes/login")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk()); // ídem
    }

    @Test
    void endpointProtegido_sinToken_debeRetornar401() throws Exception {
        mockMvc.perform(get("/api/cualquier-ruta-protegida"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void endpointProtegido_conTokenValido_debePermitirAcceso() throws Exception {
        String tokenFalso = "Bearer token.valido.mock";

        // Configura el mock para que el token sea válido
        org.mockito.Mockito.when(jwtService.extractUsername("token.valido.mock"))
                .thenReturn("usuario@test.com");
        org.mockito.Mockito.when(jwtService.isTokenValid(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any()))
                .thenReturn(true);

        mockMvc.perform(get("/api/cualquier-ruta-protegida")
                        .header("Authorization", tokenFalso))
                .andExpect(status().isOk()); // acceso concedido
    }
}