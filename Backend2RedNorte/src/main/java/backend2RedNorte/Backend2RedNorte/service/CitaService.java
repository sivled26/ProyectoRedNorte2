package backend2RedNorte.Backend2RedNorte.service;

import backend2RedNorte.Backend2RedNorte.model.Cita;
import backend2RedNorte.Backend2RedNorte.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    public Cita agendar(Cita cita) {
        return citaRepository.save(cita);
    }

    public List<Cita> listarPorPaciente(String correo) {
        return citaRepository.findByPacienteCorreo(correo);
    }

    public void cancelar(Long id) {
        citaRepository.deleteById(id);
    }
}

