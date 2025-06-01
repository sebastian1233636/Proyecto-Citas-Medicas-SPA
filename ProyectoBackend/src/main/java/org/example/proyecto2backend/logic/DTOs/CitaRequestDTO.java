package org.example.proyecto2backend.logic.DTOs;

public record CitaRequestDTO(
        String medicoId,
        String fecha,    // formato: d/M/yy  (ej: 24/5/25)
        String hora      // formato: HH:mm   (ej: 09:00)
) {
}
