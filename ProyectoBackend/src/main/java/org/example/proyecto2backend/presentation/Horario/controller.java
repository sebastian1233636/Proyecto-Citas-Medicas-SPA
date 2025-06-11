package org.example.proyecto2backend.presentation.Horario;

import org.example.proyecto2backend.logic.DTOs.EliminarHorarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.proyecto2backend.logic.Horario;
import org.example.proyecto2backend.logic.service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("horariosController")
@RequestMapping("/api/horarios")
public class controller {
    @Autowired
    private service service;

    @GetMapping("/medico/{medicoId}")
    public List<Horario> obtenerPorMedico(@PathVariable Integer medicoId) {
        return service.obtenerHorariosPorMedico(medicoId.toString());
    }

    @PostMapping("/eliminar")
    public void eliminarHorario(@RequestBody EliminarHorarioDTO dto) {
        service.eliminarHorario(dto.id(), dto.dia());
    }
}