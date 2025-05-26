package org.example.proyecto2backend.presentation.citas;

import lombok.AllArgsConstructor;
import org.example.proyecto2backend.logic.Cita;
import org.example.proyecto2backend.logic.DTOs.CitaRequestDTO;
import org.example.proyecto2backend.logic.DTOs.NotaCitaDTO;
import org.example.proyecto2backend.logic.DTOs.ReservaRequestDTO;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController("citasController")
@AllArgsConstructor
@RequestMapping("/citas")
public class controller {
    @Autowired
    service service;

    @GetMapping("/confirmar")
    public ResponseEntity<?> confirmar(@RequestParam("did") String doctorId,
                                       @RequestParam("ddt") String dateTime) {
        Medico medico = service.obtenerMedicoPorId(doctorId);

        if (medico == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Médico no encontrado"));
        }

        String[] dateTimeParts = dateTime.split("T");
        String fecha;
        String hora;

        if (dateTimeParts.length == 2) {
            fecha = dateTimeParts[0];
            hora = dateTimeParts[1];
        } else {
            fecha = "Fecha no válida";
            hora = "Hora no válida";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("medico", medico);
        response.put("fecha", fecha);
        response.put("hora", hora);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/appointment/confirm")
    public ResponseEntity<?> agendarCita(@RequestBody CitaRequestDTO request,
                                         @AuthenticationPrincipal(expression = "usuario") Usuario usuario) {
        try {
            Medico medico = service.obtenerMedicoPorId(request.medicoId());
            if (medico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Médico no encontrado"));
            }

            DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("d/M/yy");
            DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate fecha = LocalDate.parse(request.fecha(), formatterFecha);
            LocalTime hora = LocalTime.parse(request.hora(), formatterHora);

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
                    .body(Map.of("error", "Formato de fecha u hora inválido", "detalle", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al agendar la cita", "detalle", e.getMessage()));
        }
    }


    @PostMapping("/book")
    public ResponseEntity<?> redirigirReserva(@RequestBody ReservaRequestDTO request) {
        try {
            String encodedDoctorId = URLEncoder.encode(request.doctorId(), StandardCharsets.UTF_8);
            String encodedDateTime = URLEncoder.encode(request.dateTime(), StandardCharsets.UTF_8);

            String redirectionUrl = "/confirmar?did=" + encodedDoctorId + "&ddt=" + encodedDateTime;

            return ResponseEntity.ok(Map.of("redirectUrl", redirectionUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo generar la URL de redirección", "detalle", e.getMessage()));
        }
    }





}


