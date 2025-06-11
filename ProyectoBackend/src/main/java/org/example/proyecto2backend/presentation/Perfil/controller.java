package org.example.proyecto2backend.presentation.Perfil;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.proyecto2backend.logic.DTOs.PerfilUsuarioDTO;
import org.example.proyecto2backend.logic.DTOs.PerfilMedicoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.proyecto2backend.logic.DTOs.HorarioDTO;
import org.springframework.security.oauth2.jwt.Jwt;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.example.proyecto2backend.logic.Horario;
import org.example.proyecto2backend.logic.Medico;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.List;

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

            if (rol == 2) {
                List<Horario> horarios = service.obtenerHorariosPorMedico(sessionUser.getId());
                Medico med =service.obtenerMedicoPorId(sessionUser.getId());

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

            } else if (rol == 1) {
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
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el perfil: " + ex.getMessage());
        }
    }

    @DeleteMapping("/eliminarHorario/{id}/{dia}")
    public ResponseEntity<?> eliminarHorario(@PathVariable("id") Integer id, @PathVariable("dia") String dia) {
        try {
            service.eliminarHorario(id, dia);
            return ResponseEntity.ok("Horario eliminado correctamente");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el horario: " + ex.getMessage());
        }
    }

    @PostMapping("/agregarHorario/{medicoId}/{dia}/{horaInicio}/{horaFin}")
    public ResponseEntity<String> agregarHorario(@PathVariable String medicoId, @PathVariable String dia, @PathVariable String horaInicio, @PathVariable String horaFin) {
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

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarPerfil(@AuthenticationPrincipal Jwt jwt, @RequestBody PerfilMedicoDTO perfilMedicoDTO) {
        try {
            String id = jwt.getClaimAsString("id");

            if (id == null || id.isBlank()) { return ResponseEntity.badRequest().body("Id de usuario no encontrado en el token."); }

            Medico medico = service.obtenerMedicoPorId(id);

            if (medico == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Médico no encontrado."); }
            if (perfilMedicoDTO.especialidad() != null) { medico.setEspecialidad(perfilMedicoDTO.especialidad()); }
            if (perfilMedicoDTO.costo() != null) { medico.setCosto(perfilMedicoDTO.costo()); }
            if (perfilMedicoDTO.localidad() != null) { medico.setLocalidad(perfilMedicoDTO.localidad()); }
            if (perfilMedicoDTO.frecuenciaCitas() != null) { medico.setFrecuenciaCitas(perfilMedicoDTO.frecuenciaCitas()); }

            service.actualizarMedico(medico);
            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + ex.getMessage());
        }
    }
}