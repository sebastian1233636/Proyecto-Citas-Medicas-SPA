package org.example.proyecto2backend.presentation.citas;

import lombok.AllArgsConstructor;
import org.example.proyecto2backend.logic.Cita;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController("citasController")
@AllArgsConstructor
@RequestMapping("/citas")
public class controller {
    @Autowired
    service service;

    @PostMapping("/appointment/confirm")
    public ResponseEntity<?> agendarCita(@RequestParam("did") String medicoId,
                                         @RequestParam("ddt") String fechaHora,
                                         @AuthenticationPrincipal Jwt jwt) {
        try {
            Medico medico = service.obtenerMedicoPorId(medicoId);
            if (medico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Médico no encontrado"));
            }

            String usuarioId = jwt.getClaimAsString("id");
            Usuario usuario = service.findUsuarioById(usuarioId); // método que recupera desde DB

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no válido"));
            }

            String[] partes = fechaHora.split("T");
            if (partes.length != 2) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Formato de fecha y hora inválido"));
            }

            DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(partes[0], formatterFecha);

            DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime hora = LocalTime.parse(partes[1], formatterHora);

            Cita cita = new Cita();
            cita.setMedico(medico);
            cita.setUsuario(usuario);
            cita.setFecha(fecha);
            cita.setHora(hora);
            cita.setStatus("Pendiente");

            service.agendarCita(cita);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Cita agendada exitosamente"));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al parsear fecha u hora", "detalle", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }
}