package backend2RedNorte.Backend2RedNorte.controller;

import backend2RedNorte.Backend2RedNorte.model.Cita;
import backend2RedNorte.Backend2RedNorte.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping("/agendar")
    public Cita agendar(@RequestBody Cita cita) {
        return citaService.agendar(cita);
    }

    @GetMapping("/listar/{correo}")
    public List<Cita> listar(@PathVariable String correo) {
        return citaService.listarPorPaciente(correo);
    }

    @DeleteMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return "Cita cancelada con éxito";
    }
}
