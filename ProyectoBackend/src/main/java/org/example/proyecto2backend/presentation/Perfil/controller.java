package org.example.proyecto2backend.presentation.Perfil;

import org.example.proyecto2backend.logic.DTOs.HorarioDTO;
import org.example.proyecto2backend.logic.DTOs.MedicoDTO;
import org.example.proyecto2backend.logic.DTOs.PerfilMedicoDTO;
import org.example.proyecto2backend.logic.DTOs.PerfilUsuarioDTO;
import org.example.proyecto2backend.logic.Horario;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController("perfilController")
@RequestMapping("/api/perfil")
public class controller {

    @Autowired
    private service service;

    @GetMapping("/miPerfil")
    public ResponseEntity<?> getMiPerfil(@AuthenticationPrincipal Jwt jwt) {
        try {
            String id = jwt.getClaimAsString("id");
            Integer rol = jwt.getClaim("rol") != null ? ((Long) jwt.getClaim("rol")).intValue() : null;
            System.out.println("Rol: " + rol);

            if (id == null || id.isBlank()) {
                return ResponseEntity.badRequest().body("ID de usuario no encontrado en el token.");
            }

            if (rol == null) {
                return ResponseEntity.badRequest().body("Rol no encontrado en el token.");
            }

            Usuario sessionUser = service.findUsuarioById(id);
            if (sessionUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            if (rol == 2) { // Médico
                List<Horario> horarios = service.obtenerHorariosPorMedico(sessionUser.getId());
                Medico med =service.obtenerMedicoPorId(sessionUser.getId());

                // Convertir a HorarioDTO
                List<HorarioDTO> horariosDTO = horarios.stream()
                        .map(h -> new HorarioDTO(
                                h.getId(),
                                h.getDia(),
                                h.getHoraInicio(),
                                h.getHoraFin()
                        ))
                        .collect(Collectors.toList());

                PerfilMedicoDTO perfilDTO = new PerfilMedicoDTO(
                        sessionUser.getId(),
                        sessionUser.getNombre(),
                        med.getEspecialidad(),
                        med.getCosto(),
                        med.getLocalidad(),
                        med.getFrecuenciaCitas(),
                        horariosDTO
                );

                return ResponseEntity.ok(perfilDTO);

            } else if (rol == 1) { // Usuario regular
                PerfilUsuarioDTO perfilDTO = new PerfilUsuarioDTO(
                        sessionUser.getId(),
                        sessionUser.getNombre()
                );
                return ResponseEntity.ok(perfilDTO);

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Rol no autorizado.");
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // Reemplázalo con un logger si lo tienes
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el perfil: " + ex.getMessage());
        }
    }


    @PostMapping("/actualizar")
    public Medico actualizarPerfil(@RequestBody MedicoDTO dto) {
        return service.actualizarMedico(
                dto.id(),
                dto.especialidad(),
                dto.costo(),
                dto.localidad(),
                dto.frecuenciaCitas()
        );
    }


    @DeleteMapping("/eliminarHorario/{id}/{dia}")
    public ResponseEntity<?> eliminarHorario(@PathVariable("id") Integer id,
                                             @PathVariable("dia") String dia) {
        try {
            service.eliminarHorario(id, dia);
            return ResponseEntity.ok("Horario eliminado correctamente");
        } catch (Exception ex) {
            ex.printStackTrace(); // Considera usar logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el horario: " + ex.getMessage());
        }
    }


    @PostMapping("/agregarHorario/{medicoId}/{dia}/{horaInicio}/{horaFin}")
    public ResponseEntity<String> agregarHorario(@PathVariable String medicoId,
                                                 @PathVariable String dia,
                                                 @PathVariable String horaInicio,
                                                 @PathVariable String horaFin) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime inicio = LocalTime.parse(horaInicio, formatter);
            LocalTime fin = LocalTime.parse(horaFin, formatter);

            service.agregarHorario(medicoId, dia, inicio, fin);
            return ResponseEntity.ok("Horario agregado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error al agregar horario: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error inesperado: " + e.getMessage());
        }
    }



}
