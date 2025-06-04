package org.example.proyecto2backend.presentation.Historial;

import lombok.AllArgsConstructor;
import org.example.proyecto2backend.data.CitaRepository;
import org.example.proyecto2backend.data.UsuarioRepository;
import org.example.proyecto2backend.logic.Cita;
import org.example.proyecto2backend.logic.DTOs.CitaResponseDTO;
import org.example.proyecto2backend.logic.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("historialController")
@AllArgsConstructor
@RequestMapping("/historialPaciente")
public class Controller {

    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    //  Obtener historial de citas (con o sin filtros), ordenado por fecha y hora
    @GetMapping("/inicio")
    public ResponseEntity<List<CitaResponseDTO>> historialCitas(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "") String doctor
    ) {
        List<Cita> citas = citaRepository
                .findByUsuarioIdAndStatusContainingIgnoreCaseAndMedicoUsuarioNombreContainingIgnoreCaseOrderByFechaDescHoraDesc(
                        usuario.getId(), status, doctor);

        List<CitaResponseDTO> respuesta = citas.stream()
                .map(CitaResponseDTO::new)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    //  Ver notas de una cita específica (por ID)
    @GetMapping("/{id}/notas")
    public ResponseEntity<String> verNotas(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer id
    ) {
        return citaRepository.findById(String.valueOf(id))
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()))
                .map(cita -> ResponseEntity.ok(cita.getNotas() != null ? cita.getNotas() : ""))
                .orElse(ResponseEntity.notFound().build());
    }

    //  Aceptar cita (por ID)
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<String> aceptarCita(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer id
    ) {
        return citaRepository.findById(String.valueOf(id))
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()))
                .map(cita -> {
                    cita.setStatus("Aceptada");
                    citaRepository.save(cita);
                    return ResponseEntity.ok("Cita aceptada");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //  Cancelar cita (por ID)
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarCita(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer id
    ) {
        return citaRepository.findById(String.valueOf(id))
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()))
                .map(cita -> {
                    cita.setStatus("Cancelada");
                    citaRepository.save(cita);
                    return ResponseEntity.ok("Cita cancelada");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


