package org.example.proyecto2backend.presentation.Perfil;

import org.example.proyecto2backend.logic.DTOs.MedicoDTO;
import org.example.proyecto2backend.logic.Medico;
import org.example.proyecto2backend.logic.Usuario;
import org.example.proyecto2backend.logic.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController("perfilController")
@RequestMapping("/api/perfil")
public class controller {

    @Autowired
    private service service;

    @GetMapping
    public Usuario obtenerPerfil(Principal principal) {
        String username = principal.getName();
        return service.buscarUsuario(username);
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
}
