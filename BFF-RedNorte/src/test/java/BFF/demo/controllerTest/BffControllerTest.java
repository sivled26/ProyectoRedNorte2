package BFF.demo.controllerTest;

import BFF.demo.controller.BffController; // Asegúrate de que esta ruta apunte a tu controlador
import BFF.demo.dto.AuthResponse;
import BFF.demo.dto.Cita;
import BFF.demo.dto.LoginPacienteRequest;
import BFF.demo.dto.RegistroPacienteRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf; // 👈 CSRF versión Reactiva

@WebFluxTest(BffController.class)
class BffControllerTest {

    @Autowired
    private WebTestClient webTestClient; // Corregido el doble punto y coma

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private WebClient.Builder webClientBuilder;

    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void setUp() {
        exchangeFunction = mock(ExchangeFunction.class);

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private void mockBffBackendResponse(Object bodyResponse, HttpStatus status) throws JsonProcessingException {
        String jsonResult = objectMapper.writeValueAsString(bodyResponse);

        ClientResponse clientResponse = ClientResponse.create(status)
                .header("Content-Type", "application/json")
                .body(jsonResult)
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(clientResponse));
    }

    @Test
    @WithMockUser
    void login_DebeRetornarAuthResponseExitoso() throws Exception {
        // Arrange
        LoginPacienteRequest request = new LoginPacienteRequest();
        AuthResponse mockResponse = new AuthResponse();

        mockBffBackendResponse(mockResponse, HttpStatus.OK);

        // Act & Assert (Migrado a WebTestClient)
        webTestClient.mutateWith(csrf()) // 👈 Va primero, justo después de webTestClient
                .post()
                .uri("/bff/pacientes/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser
    void registro_DebeRetornarMensajeDeConfirmacion() throws Exception {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        String mensajeEsperado = "Usuario registrado correctamente";

        mockBffBackendResponse(mensajeEsperado, HttpStatus.OK);

        // Act & Assert
        webTestClient.mutateWith(csrf()) // 👈 Primero mutamos el cliente
                .post()
                .uri("/bff/pacientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(mensajeEsperado);
    }

    @Test
    @WithMockUser
    void agendar_DebeReenviarTokenYRetornarCita() throws Exception {
        // Arrange
        Cita citaInput = new Cita();
        mockBffBackendResponse(citaInput, HttpStatus.OK);

        // Act & Assert
        webTestClient.mutateWith(csrf()) // 👈 Primero mutamos el cliente
                .post()
                .uri("/bff/citas/agendar")
                .header("Authorization", "Bearer token-valido-123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(citaInput)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser
    void listar_DebeRetornarListaDeCitasDesdeFlux() throws Exception {
        // Arrange
        String correo = "paciente@correo.com";
        List<Cita> listaCitas = Arrays.asList(new Cita(), new Cita());

        mockBffBackendResponse(listaCitas, HttpStatus.OK);

        // Act & Assert
        webTestClient.get()
                .uri("/bff/citas/listar/{correo}", correo)
                .header("Authorization", "Bearer token-valido-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2); // 👈 Validación JSON reactiva
    }

    @Test
    @WithMockUser
    void cancelar_DebeRetornarMensajeDeExito() throws Exception {
        // Arrange
        Long idCita = 99L;
        String respuestaBackend = "Cita cancelada con éxito";

        mockBffBackendResponse(respuestaBackend, HttpStatus.OK);

        // Act & Assert
        webTestClient.mutateWith(csrf()) // 👈 Primero mutamos el cliente
                .delete()
                .uri("/bff/citas/cancelar/{id}", idCita)
                .header("Authorization", "Bearer token-valido-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(respuestaBackend);
    }
}