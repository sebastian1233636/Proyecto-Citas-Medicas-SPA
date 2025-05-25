package org.example.proyecto2backend.data;

import org.example.proyecto2backend.logic.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaRepository extends JpaRepository<Cita, String> {

    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar citas por fecha, paciente, etc.

    // List<Cita> findByFecha(Date fecha);
    // List<Cita> findByPaciente(Paciente paciente);
}
