package com.example.redNorte.demo.controllerTest;

import com.example.redNorte.demo.controller.PacientesController;
import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.service.PacientesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacientesController.class)
@AutoConfigureMockMvc(addFilters = false)
class PacientesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PacientesService pacientesService;

    @Test
    void registro_debeRetornar200ConMensaje() throws Exception {
        when(pacientesService.registrar(any()))
                .thenReturn("Paciente registrado correctamente");

        mockMvc.perform(post("/api/pacientes/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Arturo",
                                "apellido", "Santiago",
                                "correo", "arturo@mail.com",
                                "run", "12345678-9",
                                "contrasena", "1234"
                        ))))
                .andExpect(status().isOk())
                .andExpect(content().string("Paciente registrado correctamente"));
    }

    @Test
    void login_debeRetornar200ConTokenYCorreo() throws Exception {
        when(pacientesService.login(any()))
                .thenReturn(new AuthResponse("jwt-token-test", "arturo@mail.com"));

        mockMvc.perform(post("/api/pacientes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "correo", "arturo@mail.com",
                                "contrasena", "1234"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-test"))
                .andExpect(jsonPath("$.correo").value("arturo@mail.com"));
    }

    @Test
    void login_credencialesInvalidas_debeRetornar401() throws Exception {
        when(pacientesService.login(any()))
                .thenThrow(new ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        mockMvc.perform(post("/api/pacientes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "correo", "arturo@mail.com",
                                "contrasena", "wrong"
                        ))))
                .andExpect(status().isUnauthorized());
    }
}
