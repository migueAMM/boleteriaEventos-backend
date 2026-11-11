package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuponDescuentoCreateDTO(
        @NotNull(message = "El id de evento es obligatorio")
        Long idEvento,

        @NotBlank(message = "El código del cupón es obligatorio")
        String codigo,

        @NotNull(message = "El porcentaje de descuento es obligatorio")
        @DecimalMin(value = "0.01", message = "El descuento debe ser mayor a 0")
        @DecimalMax(value = "100.00", message = "El descuento no puede superar el 100%")
        BigDecimal porcentajeDescuento,

        @NotNull(message = "Los usos máximos son obligatorios")
        @Min(value = 1, message = "Debe permitir al menos 1 uso")
        Integer usosMaximos,

        LocalDateTime fechaExpiracion
) {}
