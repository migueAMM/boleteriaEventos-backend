package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlanCreateDTO(
        @NotBlank(message = "El nombre del plan es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,

        @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
        String descripcion,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede exceder 30 caracteres")
        String estado,

        @NotNull(message = "El ID del prestador es obligatorio")
        Long idPrestador
) {}
