package BFF.demo.controller;



import BFF.demo.dto.AuthResponse;
import BFF.demo.dto.Cita;
import BFF.demo.dto.LoginPacienteRequest;
import BFF.demo.dto.RegistroPacienteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/bff")
@RequiredArgsConstructor
public class BffController {

    private final WebClient.Builder webClientBuilder;

    // Pacientes
    @PostMapping("/pacientes/login")
    public AuthResponse login(@RequestBody LoginPacienteRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/api/pacientes/login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .block();
    }

    @PostMapping("/pacientes/registro")
    public String registro(@RequestBody RegistroPacienteRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/api/pacientes/registro")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // Citas
    @PostMapping("/citas/agendar")
    public Cita agendar(@RequestBody Cita cita, @RequestHeader("Authorization") String authHeader) {
        System.out.println("Entró a agendar cita con token: " + authHeader);
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8082/api/citas/agendar")
                .header("Authorization", authHeader)
                .bodyValue(cita)
                .retrieve()
                .bodyToMono(Cita.class)
                .block();
    }

    @GetMapping("/citas/listar/{correo}")
    public List<Cita> listar(@PathVariable String correo, @RequestHeader("Authorization") String authHeader) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/citas/listar/" + correo)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToFlux(Cita.class)
                .collectList()
                .block();
    }

    @DeleteMapping("/citas/cancelar/{id}")
    public String cancelar(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        return webClientBuilder.build()
                .delete()
                .uri("http://localhost:8082/api/citas/cancelar/" + id)
                .header("Authorization", authHeader) // 👈 reenviar token completo
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
