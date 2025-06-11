package org.example.proyecto2backend.logic.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record PerfilMedicoDTO(
        String id,
        String nombre,
        String especialidad,
        BigDecimal costo,
        String localidad,
        Integer frecuenciaCitas,
        List<HorarioDTO> horarios
) { }