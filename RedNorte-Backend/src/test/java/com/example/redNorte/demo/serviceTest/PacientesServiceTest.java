package com.example.redNorte.demo.serviceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.redNorte.demo.dto.LoginPacienteRequest;

import com.example.redNorte.demo.dto.RegistroPacienteRequest;
import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.model.Pacientes;
import com.example.redNorte.demo.repository.PacientesRepository;

import com.example.redNorte.demo.service.PacientesService;
import com.example.redNorte.demo.service.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PacientesServiceTest {

    @Mock
    private PacientesRepository pacientesRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PacientesService pacientesService;

    @Test
    void testRegistrarPacienteExitoso() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setNombre("Arturo");
        request.setApellido("Santiago");
        request.setCorreo("arturo@mail.com");
        request.setRun("12345678-9");
        request.setContrasena("1234");
        request.setDireccion("Melipilla");

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234"))
                .thenReturn("$2a$10$encodedPassword");

        String resultado = pacientesService.registrar(request);

        assertEquals("Paciente registrado correctamente", resultado);
        verify(pacientesRepository).save(any(Pacientes.class));
    }

    @Test
    void testRegistrar_debeMapearTodosLosCamposDelRequest() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setNombre("Arturo");
        request.setApellido("Santiago");
        request.setCorreo("arturo@mail.com");
        request.setRun("12345678-9");
        request.setContrasena("1234");
        request.setDireccion("Melipilla");

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234"))
                .thenReturn("$2a$10$encodedPassword");

        pacientesService.registrar(request);

        ArgumentCaptor<Pacientes> captor = ArgumentCaptor.forClass(Pacientes.class);
        verify(pacientesRepository).save(captor.capture());

        Pacientes guardado = captor.getValue();
        assertEquals("Arturo", guardado.getNombre());
        assertEquals("Santiago", guardado.getApellido());
        assertEquals("arturo@mail.com", guardado.getCorreo());
        assertEquals("12345678-9", guardado.getRun());
        assertEquals("$2a$10$encodedPassword", guardado.getContrasena());
        assertEquals("Melipilla", guardado.getDireccion());
    }

    @Test
    void testLoginPacienteExitoso() {
        Pacientes pacientes = Pacientes.builder()
                .correo("arturo@mail.com")
                .contrasena("$2a$10$encodedPassword")
                .build();

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.of(pacientes));
        when(passwordEncoder.matches("1234", "$2a$10$encodedPassword"))
                .thenReturn(true);
        when(jwtService.generateToken("arturo@mail.com"))
                .thenReturn("fake-jwt-token");

        LoginPacienteRequest request = new LoginPacienteRequest();
        request.setCorreo("arturo@mail.com");
        request.setContrasena("1234");

        AuthResponse response = pacientesService.login(request);

        assertNotNull(response);
        assertEquals("arturo@mail.com", response.getCorreo());
        assertEquals("fake-jwt-token", response.getToken());
        verify(pacientesRepository).findByCorreo("arturo@mail.com");
    }

    @Test
    void testRegistrarCorreoDuplicado() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setCorreo("arturo@mail.com");

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.of(new Pacientes()));

        assertThrows(RuntimeException.class, () -> pacientesService.registrar(request));
        verify(pacientesRepository, never()).save(any());
    }

    @Test
    void testRegistrarDataIntegrityViolation() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setNombre("Arturo");
        request.setApellido("Santiago");
        request.setCorreo("arturo@mail.com");
        request.setRun("12345678-9");
        request.setContrasena("1234");

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234"))
                .thenReturn("$2a$10$encodedPassword");
        when(pacientesRepository.save(any(Pacientes.class)))
                .thenThrow(new DataIntegrityViolationException("RUN duplicado"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pacientesService.registrar(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Datos inválidos"));
    }

    @Test
    void testLoginPacienteNoEncontrado() {
        LoginPacienteRequest request = new LoginPacienteRequest();
        request.setCorreo("noexiste@mail.com");
        request.setContrasena("1234");

        when(pacientesRepository.findByCorreo("noexiste@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pacientesService.login(request));
    }

    @Test
    void testLoginCredencialesInvalidas() {
        Pacientes pacientes = Pacientes.builder()
                .correo("arturo@mail.com")
                .contrasena("$2a$10$encodedPassword")
                .build();

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.of(pacientes));
        when(passwordEncoder.matches("wrong", "$2a$10$encodedPassword"))
                .thenReturn(false);

        LoginPacienteRequest request = new LoginPacienteRequest();
        request.setCorreo("arturo@mail.com");
        request.setContrasena("wrong");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pacientesService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Credenciales inválidas", ex.getReason());
        verify(jwtService, never()).generateToken(anyString());
    }

}
