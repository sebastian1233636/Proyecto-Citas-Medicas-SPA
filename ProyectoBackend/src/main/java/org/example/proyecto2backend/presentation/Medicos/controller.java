package org.example.proyecto2backend.presentation.Medicos;

import org.example.proyecto2backend.Security.UserInfo;
import org.example.proyecto2backend.logic.DTOs.MedicoResponseDTO;
import org.example.proyecto2backend.logic.DTOs.MedicoDTOGestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.proyecto2backend.data.UsuarioRepository;
import org.example.proyecto2backend.data.MedicoRepository;
import org.example.proyecto2backend.logic.DTOs.MedicoDTO;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.example.proyecto2backend.logic.Medico;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("medicosController")
@AllArgsConstructor
@RequestMapping("/Medico")
public class controller {
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    MedicoRepository medicoRepository;
    @Autowired
    service service;

    @PostMapping("/register/{id}")
    public ResponseEntity<?> register(@PathVariable String id, @RequestBody MedicoDTO dto) {
        try {
            Usuario user = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!user.getRol().getId().equals(2)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("El usuario no tiene rol de médico.");
            }

            if (medicoRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El médico ya está registrado.");
            }

            Medico medico = new Medico();
            medico.setUsuario(user);
            medico.setCosto(dto.costo());
            medico.setEspecialidad(dto.especialidad());
            medico.setFrecuenciaCitas(dto.frecuenciaCitas());
            medico.setLocalidad(dto.localidad());
            medico.setStatus("Pendiente");

            Medico guardado = medicoRepository.save(medico);
            return ResponseEntity.ok(new MedicoResponseDTO(guardado));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el médico: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<?> obtenerHorarioMedico(
            @PathVariable String id,
            @RequestParam(value = "semana", required = false, defaultValue = "0") int semana) {

        try {
            Medico medico = service.obtenerMedicoPorId(id);

            if (medico == null || !"Aprobado".equalsIgnoreCase(medico.getStatus())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Médico no encontrado o no aprobado.");
            }

            Map<LocalDate, List<String>> fechas = medico.getFechas(semana);

            Map<String, Object> response = new HashMap<>();
            response.put("id", medico.getId());
            response.put("nombre", medico.getUsuario().getNombre());
            response.put("especialidad", medico.getEspecialidad());
            response.put("costo", medico.getCosto());
            response.put("localidad", medico.getLocalidad());
            response.put("frecuenciaCitas", medico.getFrecuenciaCitas());
            response.put("disponibilidad", fechas);
            response.put("semana", semana);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el horario del médico: " + e.getMessage());
        }
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> home(@RequestParam(value = "semana", required = false, defaultValue = "0") int semana, @RequestParam(value = "error", required = false) String error) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Map<LocalDate, List<String>>> disponibilidad = new HashMap<>();

            List<Medico> medicos = (List<Medico>) service.medicoFindAll();

            List<Medico> medicosAprobados = medicos.stream()
                    .filter(m -> "Aprobado".equalsIgnoreCase(m.getStatus()))
                    .toList();

            for (Medico medico : medicosAprobados) {
                Map<LocalDate, List<String>> fechas = medico.getFechas(semana);
                disponibilidad.put(medico.getId(), fechas);
            }

            List<MedicoDTO> medicosDTO = medicosAprobados.stream()
                    .map(m -> new MedicoDTO(
                            m.getId(),
                            m.getUsuario().getNombre(),
                            m.getEspecialidad(),
                            m.getCosto(),
                            m.getLocalidad(),
                            m.getFrecuenciaCitas()
                    ))
                    .toList();

            response.put("medicos", medicosDTO);
            response.put("disponibilidad", disponibilidad);
            response.put("semana", semana);

            if (error != null) {
                response.put("error", "El horario seleccionado ya está ocupado. Por favor, elige otro.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Ocurrió un error al procesar la solicitud.");
            response.put("detalles", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/home/filtrado")
    public ResponseEntity<Map<String, Object>> filtrarMedicos(
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String localidad,
            @RequestParam(value = "semana", required = false, defaultValue = "0") int semana) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<Medico> medicosFiltrados = service.FiltradoMedicos(especialidad, localidad);

            List<Medico> medicosAprobados = medicosFiltrados.stream()
                    .filter(m -> "Aprobado".equalsIgnoreCase(m.getStatus()))
                    .toList();

            List<MedicoDTO> medicosDTO = medicosAprobados.stream()
                    .map(m -> new MedicoDTO(
                            m.getId(),
                            m.getUsuario().getNombre(),
                            m.getEspecialidad(),
                            m.getCosto(),
                            m.getLocalidad(),
                            m.getFrecuenciaCitas()
                    ))
                    .toList();

            Map<String, Map<LocalDate, List<String>>> disponibilidad = new HashMap<>();
            for (Medico medico : medicosAprobados) {
                disponibilidad.put(medico.getId(), medico.getFechas(semana));
            }

            response.put("medicos", medicosDTO);
            response.put("disponibilidad", disponibilidad);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al filtrar médicos.");
            response.put("detalles", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/gestion")
    public ResponseEntity<?> showMedicos() {
        try {
            List<Medico> medicos = service.ObtenerMedicosPendientes();

            if (medicos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron médicos registrados.");
            }

            List<MedicoDTOGestion> medicosDTO = medicos.stream()
                    .map(m -> new MedicoDTOGestion(
                            m.getId(),
                            m.getUsuario().getNombre(),
                            m.getEspecialidad(),
                            m.getCosto(),
                            m.getLocalidad(),
                            m.getFrecuenciaCitas(),
                            m.getStatus()
                    ))
                    .toList();

            return ResponseEntity.ok(medicosDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la lista de médicos: " + e.getMessage());
        }
    }

    @PutMapping("/gestion/{id}")
    public ResponseEntity<?> aprobarMedico(@PathVariable String id){
        try {
            Medico medico = service.obtenerMedicoPorId(id);

            if (medico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Médico no encontrado o ya aprobado.");
            }

            service.aceptarMedico(medico.getId());
            return ResponseEntity.ok(new MedicoResponseDTO(medico));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aprobar el médico: " + e.getMessage());
        }
    }
}