package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ComboCreateDTO(
        @NotBlank(message = "El nombre del combo es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
        BigDecimal precio,

        @NotNull(message = "Los días de vigencia son obligatorios")
        @Min(value = 1, message = "Los días de vigencia deben ser al menos 1")
        Integer diasVigencia,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede exceder 30 caracteres")
        String estado,

        List<Long> idsAtracciones
) {}
