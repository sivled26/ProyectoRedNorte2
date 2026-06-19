package com.example.redNorte.demo.configTest;

import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.service.JwtService;
import com.example.redNorte.demo.service.PacientesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PacientesService pacientesService;

    @Test
    void registroEndpoint_debeSerPublico() throws Exception {
        when(pacientesService.registrar(any())).thenReturn("Paciente registrado correctamente");

        mockMvc.perform(post("/api/pacientes/registro")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void loginEndpoint_debeSerPublico() throws Exception {
        when(pacientesService.login(any()))
                .thenReturn(new AuthResponse("token.mock", "usuario@test.com"));

        mockMvc.perform(post("/api/pacientes/login")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void endpointProtegido_sinToken_debeRetornar403() throws Exception {
        mockMvc.perform(get("/api/ruta-protegida"))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointProtegido_conTokenValido_debePermitirAcceso() throws Exception {
        when(jwtService.extractCorreo(eq("token.valido.mock")))
                .thenReturn("usuario@test.com");

        mockMvc.perform(get("/api/ruta-protegida")
                        .header("Authorization", "Bearer token.valido.mock"))
                .andExpect(status().isNotFound());
    }
}
