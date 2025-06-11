package org.example.proyecto2backend.presentation.citas;

import org.example.proyecto2backend.Security.UserInfo;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.example.proyecto2backend.logic.Medico;
import org.springframework.web.bind.annotation.*;
import org.example.proyecto2backend.logic.Cita;
import java.time.format.DateTimeParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController("citasController")
@AllArgsConstructor
@RequestMapping("/citas")
public class controller {

    private final service service;

    @PostMapping("/appointment/confirm")
    public ResponseEntity<?> agendarCita(@RequestParam("did") String medicoId, @RequestParam("ddt") String fechaHora) {
        try {
            Medico medico = service.obtenerMedicoPorId(medicoId);
            if (medico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Médico no encontrado"));
            }

            String usuarioId = UserInfo.getUsuarioId();
            if (usuarioId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            Usuario usuario = service.findUsuarioById(usuarioId);
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