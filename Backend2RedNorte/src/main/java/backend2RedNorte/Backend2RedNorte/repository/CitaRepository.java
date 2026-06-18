package backend2RedNorte.Backend2RedNorte.repository;

import backend2RedNorte.Backend2RedNorte.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPacienteCorreo(String correo);
}