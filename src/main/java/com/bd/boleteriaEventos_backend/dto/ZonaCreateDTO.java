package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ZonaCreateDTO(
        @NotBlank(message = "El nombre de la zona es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @NotNull(message = "El aforo es obligatorio")
        @Min(value = 1, message = "El aforo debe ser mayor a 0")
        Integer aforo,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        BigDecimal precio,

        @NotNull(message = "El ID de la función es obligatorio")
        Long idFuncion
) {}
