package backend2RedNorte.Backend2RedNorte.ControllerTest;

import backend2RedNorte.Backend2RedNorte.controller.CitaController;
import backend2RedNorte.Backend2RedNorte.model.Cita;
import backend2RedNorte.Backend2RedNorte.service.CitaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest levanta solo la capa Web (Controlador), ignorando la base de datos real
@WebMvcTest(CitaController.class)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Nos permite simular peticiones HTTP (GET, POST, DELETE)

    @MockitoBean
    private CitaService citaService; // Simulamos el comportamiento del servicio

    @Autowired
    private ObjectMapper objectMapper; // Convierte objetos Java a JSON de forma automática

    @Test
    @WithMockUser // Simula un usuario autenticado para saltar los filtros de seguridad
    void agendar_DebeRegistrarCitaYRetornarEstadoOk() throws Exception {
        // Arrange
        Cita citaInput = new Cita();
        Cita citaSaved = new Cita();

        when(citaService.agendar(any(Cita.class))).thenReturn(citaSaved);

        // Act & Assert
        mockMvc.perform(post("/api/citas/agendar")
                        .with(csrf()) // Evita fallos si la protección CSRF está activa
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citaInput))) // Envía el JSON simulado
                .andExpect(status().isOk()); // Valida respuesta HTTP 200

        verify(citaService, times(1)).agendar(any(Cita.class));
    }

    @Test
    @WithMockUser
    void listar_DebeRetornarListaDeCitasDelPaciente() throws Exception {
        // Arrange
        String correoTest = "juan@correo.com";
        List<Cita> listaSimulada = Arrays.asList(new Cita(), new Cita());

        when(citaService.listarPorPaciente(correoTest)).thenReturn(listaSimulada);

        // Act & Assert
        mockMvc.perform(get("/api/citas/listar/{correo}", correoTest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // Verifica que devuelva un arreglo JSON de 2 elementos

        verify(citaService, times(1)).listarPorPaciente(correoTest);
    }

    @Test
    @WithMockUser
    void cancelar_DebeEliminarCitaYDevolverMensaje() throws Exception {
        // Arrange
        Long idCita = 1L;
        doNothing().when(citaService).cancelar(idCita);

        // Act & Assert
        mockMvc.perform(delete("/api/citas/cancelar/{id}", idCita)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cita cancelada con éxito")); // Valida el String exacto de retorno

        verify(citaService, times(1)).cancelar(idCita);
    }
}