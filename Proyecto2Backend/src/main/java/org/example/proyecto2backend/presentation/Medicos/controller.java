package org.example.proyecto2backend.presentation.Medicos;


import lombok.AllArgsConstructor;
import org.example.proyecto2backend.data.MedicoRepository;
import org.example.proyecto2backend.data.UsuarioRepository;
import org.example.proyecto2backend.logic.DTOs.MedicoDTO;
import org.example.proyecto2backend.logic.DTOs.MedicoResponseDTO;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("medicosController")
@AllArgsConstructor
@RequestMapping("/Medico")
public class controller {
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    MedicoRepository medicoRepository;


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
            medico.setCosto(dto.costo());
            medico.setEspecialidad(dto.especialidad());
            medico.setFrecuenciaCitas(dto.frecuenciaCitas());
            medico.setLocalidad(dto.localidad());
            medico.setStatus("Pendiente");
            medico.setUsuario(user);

            Medico guardado = medicoRepository.save(medico);
            return ResponseEntity.ok(new MedicoResponseDTO(guardado));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el médico: " + e.getMessage());
        }
    }


}
