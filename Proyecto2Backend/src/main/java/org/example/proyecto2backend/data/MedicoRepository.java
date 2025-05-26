package org.example.proyecto2backend.data;

import org.example.proyecto2backend.logic.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String> {
    List<Medico> findByEspecialidadContainingIgnoreCaseAndLocalidadContainingIgnoreCase(String especialidad, String localidad);

    List<Medico> findByStatusContainingIgnoreCase(String status);

    Medico findByUsuarioNombreContainingIgnoreCase(String doctor);

}
