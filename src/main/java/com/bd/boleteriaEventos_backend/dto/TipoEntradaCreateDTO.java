package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TipoEntradaCreateDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @NotNull(message = "El porcentaje de descuento es obligatorio")
        @DecimalMin(value = "0.0", message = "El porcentaje no puede ser menor a 0")
        @DecimalMax(value = "100.0", message = "El porcentaje no puede ser mayor a 100")
        BigDecimal porcentajeDescuento,

        @NotNull(message = "Debe indicar si requiere adulto")
        Boolean requiereAdulto,
        Boolean esAdulto
) {}
