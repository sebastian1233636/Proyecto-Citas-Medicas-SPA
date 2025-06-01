package org.example.proyecto2backend.logic.DTOs;

import org.example.proyecto2backend.logic.Medico;

public record MedicoResponseDTO(String id, String especialidad) {
    public MedicoResponseDTO(Medico medico) {
        this(medico.getId(), medico.getEspecialidad());
    }
}
