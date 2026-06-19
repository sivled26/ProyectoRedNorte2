package backend2RedNorte.Backend2RedNorte.ServiceTest;

import backend2RedNorte.Backend2RedNorte.model.Cita;
import backend2RedNorte.Backend2RedNorte.repository.CitaRepository;
import backend2RedNorte.Backend2RedNorte.service.CitaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Usamos la extensión de Mockito para que sea un test rápido, aislado y sin cargar Spring
@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository; // Simulamos el repositorio

    @InjectMocks
    private CitaService citaService; // Mockito inyectará automáticamente el "citaRepository" mockeado aquí

    @Test
    void agendar_debeGuardarYRetornarCitaExitosamente() {
        // 1. Arrange (Preparar los datos de entrada y salida simulada)
        Cita citaMock = new Cita();
        // Si tu modelo Cita tiene setters o constructor, puedes asignarle valores aquí:
        // citaMock.setMotivo("Consulta General");

        when(citaRepository.save(any(Cita.class))).thenReturn(citaMock);

        // 2. Act (Ejecutar el método real del servicio)
        Cita resultado = citaService.agendar(citaMock);

        // 3. Assert (Verificar que se comportó como queríamos)
        assertNotNull(resultado);
        verify(citaRepository, times(1)).save(citaMock); // Asegura que se llamó al repositorio exactamente 1 vez
    }

    @Test
    void listarPorPaciente_debeRetornarListaDeCitas() {
        // 1. Arrange
        String correoTest = "paciente@correo.com";
        List<Cita> listaSimulada = Arrays.asList(new Cita(), new Cita());

        when(citaRepository.findByPacienteCorreo(correoTest)).thenReturn(listaSimulada);

        // 2. Act
        List<Cita> resultado = citaService.listarPorPaciente(correoTest);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size()); // Comprueba que devolvió los 2 elementos mockeados
        verify(citaRepository, times(1)).findByPacienteCorreo(correoTest);
    }

    @Test
    void cancelar_debeLlamarAlRepositoryParaEliminarPorId() {
        // 1. Arrange
        Long idCitaTest = 10L;
        // Como deleteById es un método 'void', no requiere un 'when(...).thenReturn(...)' ordinario.
        // Mockito por defecto no hace nada al llamarlo, lo cual es perfecto.

        // 2. Act
        citaService.cancelar(idCitaTest);

        // 3. Assert
        verify(citaRepository, times(1)).deleteById(idCitaTest); // Valida que el id correcto fue enviado a borrar
    }
}