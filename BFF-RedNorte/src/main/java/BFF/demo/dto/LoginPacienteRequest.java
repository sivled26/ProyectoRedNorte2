package BFF.demo.dto;

import lombok.Data;

@Data
public class LoginPacienteRequest {
    private String correo;
    private String contrasena;
}
