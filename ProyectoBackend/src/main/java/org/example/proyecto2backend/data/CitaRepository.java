package org.example.proyecto2backend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, String> {
    List<Cita> findByUsuarioId(String usuarioId);

    Iterable<Cita> findByMedicoId(String id);
    Iterable<Cita> findByStatusAndMedicoId(String status, String id);
    Iterable<Cita> findByStatusAndUsuarioId(String status, String id);
    Iterable<Cita> findByStatus(String status);

    @Query("SELECT c.hora FROM Cita c WHERE c.medico = :medico AND c.fecha = :fecha")
    List<LocalTime> findOcupadosByMedicoAndFecha(@Param("medico") Medico medico, @Param("fecha") LocalDate fecha);

    List<Cita> findByStatusAndMedicoIdAndUsuarioNombreContainingIgnoreCase(String status, String medicoId, String nombrePaciente);
    List<Cita> findByStatusAndUsuarioIdAndMedicoUsuarioNombreContainingIgnoreCase(String status, String usuarioId, String nombreMedico);
    List<Cita> findByMedicoUsuarioNombreContainingIgnoreCaseAndUsuarioId(String nombreMedico, String usuarioId);
    List<Cita> findByUsuarioNombreContainingIgnoreCaseAndMedicoId(String nombrePaciente, String medicoId);
}