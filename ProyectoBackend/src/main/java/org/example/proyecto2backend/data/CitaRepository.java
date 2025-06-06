package org.example.proyecto2backend.data;

import org.example.proyecto2backend.logic.Cita;
import org.example.proyecto2backend.logic.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, String> {

    List<Cita> findByUsuarioId(String usuarioId);

    Iterable<Cita> findByStatusContainingAndMedicoUsuarioNombreContainingIgnoreCase(String status, String doctor);

    Iterable<Cita> findByMedicoId(String id);

    Iterable<Cita> findByStatusAndMedicoId(String status, String id);

    Iterable<Cita> findByMedicoUsuarioNombreContainingIgnoreCaseAndMedicoId(String doctor, String id);

    Iterable<Cita> findByStatusAndUsuarioId(String status, String id);

    Iterable<Cita> findByUsuarioNombreContainingIgnoreCaseAndUsuarioId(String paciente, String id);

    Iterable<Cita> findByStatus(String status);

    @Query("SELECT c.hora FROM Cita c WHERE c.medico = :medico AND c.fecha = :fecha")
    List<LocalTime> findOcupadosByMedicoAndFecha(@Param("medico") Medico medico, @Param("fecha") LocalDate fecha);


    /**
     * Busca citas por estado, médico ID y nombre del paciente
     */
    List<Cita> findByStatusAndMedicoIdAndUsuarioNombreContainingIgnoreCase(
            String status, String medicoId, String nombrePaciente);

    /**
     * Busca citas por estado, usuario ID y nombre del médico
     */
    List<Cita> findByStatusAndUsuarioIdAndMedicoUsuarioNombreContainingIgnoreCase(
            String status, String usuarioId, String nombreMedico);

    /**
     * Busca citas por nombre del médico y usuario ID
     */
    List<Cita> findByMedicoUsuarioNombreContainingIgnoreCaseAndUsuarioId(
            String nombreMedico, String usuarioId);

    /**
     * Método personalizado para buscar citas con ordenamiento
     */
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "ORDER BY c.fecha DESC, c.hora DESC")
    List<Cita> findByMedicoIdOrderByFechaDescHoraDesc(@Param("medicoId") String medicoId);

    /**
     * Método personalizado para buscar citas de paciente con ordenamiento
     */
    @Query("SELECT c FROM Cita c WHERE c.usuario.id = :usuarioId " +
            "ORDER BY c.fecha DESC, c.hora DESC")
    List<Cita> findByUsuarioIdOrderByFechaDescHoraDesc(@Param("usuarioId") String usuarioId);

    List<Cita> findByUsuarioNombreContainingIgnoreCaseAndMedicoId(String nombrePaciente, String medicoId);
}
