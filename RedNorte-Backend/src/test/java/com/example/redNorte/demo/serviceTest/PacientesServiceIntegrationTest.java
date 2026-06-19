package com.example.redNorte.demo.serviceTest;

import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.dto.LoginPacienteRequest;
import com.example.redNorte.demo.dto.RegistroPacienteRequest;
import com.example.redNorte.demo.model.Pacientes;
import com.example.redNorte.demo.repository.PacientesRepository;
import com.example.redNorte.demo.service.PacientesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PacientesServiceIntegrationTest {

    @Autowired
    private PacientesService pacientesService;

    @Autowired
    private PacientesRepository pacientesRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        pacientesRepository.deleteAll();
    }

    @Test
    void registrar_debeGuardarPacienteEnBaseDeDatos() {
        RegistroPacienteRequest request = crearRegistroRequest(
                "arturo@mail.com", "12345678-9");

        String resultado = pacientesService.registrar(request);

        assertEquals("Paciente registrado correctamente", resultado);
        Pacientes guardado = pacientesRepository.findByCorreo("arturo@mail.com").orElseThrow();
        assertEquals("Arturo", guardado.getNombre());
        assertEquals("Santiago", guardado.getApellido());
        assertEquals("12345678-9", guardado.getRun());
        assertEquals("Melipilla", guardado.getDireccion());
        assertNotEquals("1234", guardado.getContrasena());
    }

    @Test
    void registrar_correoDuplicado_debeLanzarExcepcion() {
        pacientesService.registrar(crearRegistroRequest("arturo@mail.com", "12345678-9"));

        RegistroPacienteRequest duplicado = crearRegistroRequest("arturo@mail.com", "87654321-0");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pacientesService.registrar(duplicado));

        assertEquals("Correo ya registrado", ex.getMessage());
    }

    @Test
    void registrar_runDuplicado_debeLanzarBadRequest() {
        pacientesService.registrar(crearRegistroRequest("uno@mail.com", "12345678-9"));

        RegistroPacienteRequest mismoRun = crearRegistroRequest("dos@mail.com", "12345678-9");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> pacientesService.registrar(mismoRun));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Datos inválidos"));
    }

    @Test
    void login_debeRetornarTokenValido() {
        pacientesService.registrar(crearRegistroRequest("arturo@mail.com", "12345678-9"));

        LoginPacienteRequest login = new LoginPacienteRequest();
        login.setCorreo("arturo@mail.com");
        login.setContrasena("1234");

        AuthResponse response = pacientesService.login(login);

        assertEquals("arturo@mail.com", response.getCorreo());
        assertNotNull(response.getToken());
        assertFalse(response.getToken().isBlank());
    }

    @Test
    void login_pacienteNoExiste_debeLanzarExcepcion() {
        LoginPacienteRequest login = new LoginPacienteRequest();
        login.setCorreo("noexiste@mail.com");
        login.setContrasena("1234");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pacientesService.login(login));

        assertEquals("Paciente no encontrado", ex.getMessage());
    }

    @Test
    void login_contrasenaIncorrecta_debeLanzarUnauthorized() {
        pacientesService.registrar(crearRegistroRequest("arturo@mail.com", "12345678-9"));

        LoginPacienteRequest login = new LoginPacienteRequest();
        login.setCorreo("arturo@mail.com");
        login.setContrasena("incorrecta");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> pacientesService.login(login));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Credenciales inválidas", ex.getReason());
    }

    private RegistroPacienteRequest crearRegistroRequest(String correo, String run) {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setNombre("Arturo");
        request.setApellido("Santiago");
        request.setCorreo(correo);
        request.setRun(run);
        request.setContrasena("1234");
        request.setDireccion("Melipilla");
        return request;
    }
}
