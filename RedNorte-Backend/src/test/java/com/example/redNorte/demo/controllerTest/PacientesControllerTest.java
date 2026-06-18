package com.example.redNorte.demo.controllerTest;

import com.example.redNorte.demo.controller.PacientesController;
import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.dto.LoginPacienteRequest;
import com.example.redNorte.demo.dto.RegistroPacienteRequest;
import com.example.redNorte.demo.service.PacientesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PacientesControllerTest {

    @Mock
    private PacientesService pacientesService;

    @InjectMocks
    private PacientesController pacientesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistroPaciente() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setCorreo("arturo@mail.com");
        request.setContrasena("1234");

        when(pacientesService.registrar(request)).thenReturn("Usuario registrado con éxito");

        ResponseEntity<String> response = pacientesController.registrar(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Usuario registrado con éxito", response.getBody());
    }

    @Test
    void testLoginPaciente() {
        LoginPacienteRequest request = new LoginPacienteRequest();
        request.setCorreo("arturo@mail.com");
        request.setContrasena("1234");

        AuthResponse authResponse = new AuthResponse("fake-jwt-token", "arturo@mail.com");


        when(pacientesService.login(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = pacientesController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("arturo@mail.com", response.getBody().getCorreo());
        assertEquals("fake-jwt-token", response.getBody().getToken());
    }

}
