package com.example.redNorte.demo.controllerTest;

import com.example.redNorte.demo.repository.PacientesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PacientesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacientesRepository pacientesRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        pacientesRepository.deleteAll();
    }

    @Test
    void registroYLogin_flujoCompleto() throws Exception {
        Map<String, String> registro = Map.of(
                "nombre", "Arturo",
                "apellido", "Santiago",
                "correo", "arturo@mail.com",
                "run", "12345678-9",
                "contrasena", "1234",
                "direccion", "Melipilla"
        );

        mockMvc.perform(post("/api/pacientes/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isOk());

        Map<String, String> login = Map.of(
                "correo", "arturo@mail.com",
                "contrasena", "1234"
        );

        mockMvc.perform(post("/api/pacientes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("arturo@mail.com"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_conCredencialesInvalidas_debeRetornar401() throws Exception {
        Map<String, String> registro = Map.of(
                "nombre", "Arturo",
                "apellido", "Santiago",
                "correo", "arturo@mail.com",
                "run", "12345678-9",
                "contrasena", "1234"
        );

        mockMvc.perform(post("/api/pacientes/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isOk());

        Map<String, String> login = Map.of(
                "correo", "arturo@mail.com",
                "contrasena", "incorrecta"
        );

        mockMvc.perform(post("/api/pacientes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
