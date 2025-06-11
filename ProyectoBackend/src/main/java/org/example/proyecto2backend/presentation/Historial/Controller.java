package org.example.proyecto2backend.presentation.Historial;

import org.example.proyecto2backend.logic.DTOs.CitaCompletarDTO;
import org.example.proyecto2backend.logic.DTOs.CitaResponseDTO;
import org.example.proyecto2backend.logic.DTOs.CitaDTO;
import org.example.proyecto2backend.logic.service;
import org.springframework.web.bind.annotation.*;
import org.example.proyecto2backend.logic.Cita;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import java.util.List;

@RestController("historialController")
@AllArgsConstructor
@RequestMapping("/Historial")
public class Controller {
    private final service service;

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> obtenerCitasMedico(@PathVariable String medicoId, @RequestParam(required = false) String status, @RequestParam(required = false) String nombrePaciente) {
        try {
            List<Cita> citas = service.obtenerCitasMedico(medicoId, status, nombrePaciente);

            if (citas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron citas para los criterios especificados.");
            }

            List<CitaDTO> citasDTO = citas.stream()
                    .map(c -> new CitaDTO(
                            c.getId(),
                            c.getFecha(),
                            c.getHora(),
                            c.getStatus(),
                            c.getUsuario().getNombre(),
                            c.getMedico().getUsuario().getNombre(),
                            c.getNotas()
                    ))
                    .toList();

            return ResponseEntity.ok(citasDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las citas del médico: " + e.getMessage());
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> obtenerCitasPaciente(@PathVariable String pacienteId, @RequestParam(required = false) String status, @RequestParam(required = false) String nombreMedico) {
        try {
            List<Cita> citas = service.obtenerCitasPaciente(pacienteId, status, nombreMedico);

            if (citas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron citas para los criterios especificados.");
            }

            List<CitaDTO> citasDTO = citas.stream()
                    .map(c -> new CitaDTO(
                            c.getId(),
                            c.getFecha(),
                            c.getHora(),
                            c.getStatus(),
                            c.getUsuario().getNombre(),
                            c.getMedico().getUsuario().getNombre(),
                            c.getNotas()
                    ))
                    .toList();

            return ResponseEntity.ok(citasDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las citas del paciente: " + e.getMessage());
        }
    }

    @PutMapping("/completar/{citaId}")
    public ResponseEntity<?> completarCita(@PathVariable Integer citaId, @RequestBody CitaCompletarDTO dto) {
        try {
            Cita cita = service.obtenerCitaPorId(citaId);

            if (cita == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cita no encontrada.");
            }

            service.completarCita(citaId, dto.status(), dto.notas());

            Cita citaActualizada = service.obtenerCitaPorId(citaId);

            return ResponseEntity.ok(new CitaResponseDTO(citaActualizada));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al completar la cita: " + e.getMessage());
        }
    }

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