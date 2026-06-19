package BFF.demo.dtoTest;

import BFF.demo.dto.Cita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest // 👈 Indica a Spring que solo levante la capa de mapeo JSON (Jackson)
class CitaJsonTest {

    @Autowired
    private JacksonTester<Cita> json; // 👈 Herramienta de Spring para probar JSON de forma limpia

    @Test
    void serializacion_DebeConvertirObjetoAJsonCorrectamente() throws Exception {
        // Arrange
        Cita cita = new Cita(
                1L,
                "paciente@correo.com",
                "Dr. House",
                LocalDate.of(2026, 6, 19),
                LocalTime.of(10, 30),
                "Chequeo general"
        );

        // Act
        JsonContent<Cita> result = json.write(cita);

        // Assert (Verifica que el JSON generado tenga el formato exacto que espera el Frontend)
        assertThat(result).hasJsonPathNumberValue("$.id", 1);
        assertThat(result).hasJsonPathStringValue("$.pacienteCorreo", "paciente@correo.com");
        assertThat(result).hasJsonPathStringValue("$.doctorNombre", "Dr. House");
        assertThat(result).hasJsonPathStringValue("$.fecha", "2026-06-19"); // 👈 Crucial verificar el formato ISO
        assertThat(result).hasJsonPathStringValue("$.hora", "10:30:00");
        assertThat(result).hasJsonPathStringValue("$.motivo", "Chequeo general");
    }

    @Test
    void deserializacion_DebeConvertirJsonAObjetoCorrectamente() throws Exception {
        // Arrange
        String jsonContent = """
            {
                "id": 2,
                "pacienteCorreo": "ana@correo.com",
                "doctorNombre": "Dra. Quinn",
                "fecha": "2026-12-25",
                "hora": "15:45:00",
                "motivo": "Urgencia"
            }
            """;

        // Act
        Cita result = json.parseObject(jsonContent);

        // Assert
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getPacienteCorreo()).isEqualTo("ana@correo.com");
        assertThat(result.getDoctorNombre()).isEqualTo("Dra. Quinn");
        assertThat(result.getFecha()).isEqualTo(LocalDate.of(2026, 12, 25));
        assertThat(result.getHora()).isEqualTo(LocalTime.of(15, 45));
        assertThat(result.getMotivo()).isEqualTo("Urgencia");
    }
}