package com.example.redNorte.demo.controllerTest;

import com.example.redNorte.demo.controller.PacientesController;
import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.dto.LoginPacienteRequest;
import com.example.redNorte.demo.dto.RegistroPacienteRequest;
import com.example.redNorte.demo.service.PacientesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Le decimos a Spring que SOLO cargue este controlador, haciendo el test muy rápido
@WebMvcTest(PacientesController.class)
// Apagamos los filtros de Spring Security (addFilters = false) para no chocar con JWT aquí
@AutoConfigureMockMvc(addFilters = false)
class PacientesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Herramienta para convertir nuestros objetos Java (DTOs) a texto JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Simulamos el servicio porque no  queremos tocar la base de datos real
    @MockitoBean
    private PacientesService pacientesService;

    @Test
    void registrar_debeLlamarAlServicioYRetornarOk() throws Exception {
        // 1. Preparamos los datos de envío
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        // Asumiendo que tu DTO tiene setters o un constructor, llena algunos datos ficticios:
        // request.setNombre("Juan");
        // request.setEmail("juan@test.com");

        // 2. Le decimos al mock qué responder cuando el controlador lo llame
        String mensajeEsperado = "Paciente registrado con éxito";
        when(pacientesService.registrar(any(RegistroPacienteRequest.class))).thenReturn(mensajeEsperado);

        // 3. Ejecutamos la petición HTTP simulada
        mockMvc.perform(post("/api/pacientes/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convierte el request a JSON
                // 4. Verificamos los resultados
                .andExpect(status().isOk())
                .andExpect(content().string(mensajeEsperado));
    }

    @Test
    void login_debeLlamarAlServicioYRetornarToken() throws Exception {
        // 1. Preparamos las credenciales
        LoginPacienteRequest request = new LoginPacienteRequest();
        // request.setEmail("juan@test.com");
        // request.setPassword("12345");

        // 2. Preparamos la respuesta simulada del servicio
        // Asumiendo que AuthResponse recibe el token en su constructor o tiene setters
        AuthResponse authResponse = new AuthResponse();
        // authResponse.setToken("mock-jwt-token-123");

        when(pacientesService.login(any(LoginPacienteRequest.class))).thenReturn(authResponse);

        // 3. Ejecutamos la petición HTTP simulada
        mockMvc.perform(post("/api/pacientes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // 4. Verificamos los resultados
                .andExpect(status().isOk())
                // Si tu AuthResponse tiene un campo llamado "token", verificamos que exista en el JSON
                .andExpect(jsonPath("$.token").exists());
    }
}