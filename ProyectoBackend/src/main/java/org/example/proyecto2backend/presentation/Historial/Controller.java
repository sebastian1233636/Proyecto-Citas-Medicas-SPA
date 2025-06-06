package org.example.proyecto2backend.presentation.Historial;

import lombok.AllArgsConstructor;
import org.example.proyecto2backend.data.CitaRepository;
import org.example.proyecto2backend.data.MedicoRepository;
import org.example.proyecto2backend.data.UsuarioRepository;
import org.example.proyecto2backend.logic.DTOs.CitaDTO;
import org.example.proyecto2backend.logic.DTOs.CitaCompletarDTO;
import org.example.proyecto2backend.logic.DTOs.CitaResponseDTO;
import org.example.proyecto2backend.logic.Cita;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("historialController")
@AllArgsConstructor
@RequestMapping("/Historial")
public class Controller {

    @Autowired
    private final CitaRepository citaRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final MedicoRepository medicoRepository;
    @Autowired
    private final service service;

    // Endpoint para médicos: ver sus citas
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> obtenerCitasMedico(
            @PathVariable String medicoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nombrePaciente) {

        try {
            // Verificar que el médico existe
            Medico medico = medicoRepository.findById(medicoId)
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

            List<Cita> citas = service.obtenerCitasMedico(medicoId, status, nombrePaciente);

            if (citas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron citas para los criterios especificados.");
            }

            // Convertir a DTO
            List<CitaDTO> citasDTO = citas.stream()
                    .map(c -> new CitaDTO(
                            c.getId(),
                            c.getFecha(),
                            c.getHora(),
                            c.getStatus(),
                            c.getUsuario().getNombre(), // Nombre del paciente
                            c.getMedico().getUsuario().getNombre(), // Nombre del médico
                            c.getNotas()
                    ))
                    .toList();

            return ResponseEntity.ok(citasDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las citas del médico: " + e.getMessage());
        }
    }

    // Endpoint para pacientes: ver sus citas
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> obtenerCitasPaciente(
            @PathVariable String pacienteId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nombreMedico) {

        try {
            // Verificar que el usuario existe
            Usuario usuario = usuarioRepository.findById(pacienteId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Cita> citas = service.obtenerCitasPaciente(pacienteId, status, nombreMedico);

            if (citas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron citas para los criterios especificados.");
            }

            // Convertir a DTO
            List<CitaDTO> citasDTO = citas.stream()
                    .map(c -> new CitaDTO(
                            c.getId(),
                            c.getFecha(),
                            c.getHora(),
                            c.getStatus(),
                            c.getUsuario().getNombre(), // Nombre del paciente
                            c.getMedico().getUsuario().getNombre(), // Nombre del médico
                            c.getNotas()
                    ))
                    .toList();

            return ResponseEntity.ok(citasDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las citas del paciente: " + e.getMessage());
        }
    }

    // Endpoint para completar/actualizar una cita (solo médicos)
    @PutMapping("/completar/{citaId}")
    public ResponseEntity<?> completarCita(
            @PathVariable Integer citaId,
            @RequestBody CitaCompletarDTO dto) {

        try {
            Cita cita = service.obtenerCitaPorId(citaId);

            if (cita == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cita no encontrada.");
            }

            // Actualizar el estado y las notas
            service.completarCita(citaId, dto.status(), dto.notas());

            // Obtener la cita actualizada
            Cita citaActualizada = service.obtenerCitaPorId(citaId);

            return ResponseEntity.ok(new CitaResponseDTO(citaActualizada));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al completar la cita: " + e.getMessage());
        }
    }

    // Endpoint para obtener una cita específica por ID
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<?> obtenerCitaPorId(@PathVariable Integer citaId) {
        try {
            Cita cita = service.obtenerCitaPorId(citaId);

            if (cita == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cita no encontrada.");
            }

            CitaDTO citaDTO = new CitaDTO(
                    cita.getId(),
                    cita.getFecha(),
                    cita.getHora(),
                    cita.getStatus(),
                    cita.getUsuario().getNombre(),
                    cita.getMedico().getUsuario().getNombre(),
                    cita.getNotas()
            );

            return ResponseEntity.ok(citaDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la cita: " + e.getMessage());
        }
    }
}


