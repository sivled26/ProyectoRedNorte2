package BFF.demo.dto;

import lombok.Data;

@Data
public class RegistroPacienteRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String run;
    private String contrasena;
    private String direccion;
}
