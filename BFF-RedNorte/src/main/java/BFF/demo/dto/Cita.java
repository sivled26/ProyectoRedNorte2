package BFF.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cita {
    private Long id;
    private String pacienteCorreo;
    private String doctorNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
}
