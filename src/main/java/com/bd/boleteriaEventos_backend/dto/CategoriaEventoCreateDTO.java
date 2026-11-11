package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CategoriaEventoCreateDTO(
        @NotNull(message = "El id de evento es obligatorio")
        Long idEvento,

        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El precio base es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio base no puede ser negativo")
        BigDecimal precioBase,

        @NotNull(message = "El cargo por servicio es obligatorio")
        @DecimalMin(value = "0.0", message = "El cargo por servicio no puede ser negativo")
        BigDecimal cargoServicio,

        @NotNull(message = "El cupo máximo es obligatorio")
        @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
        Integer cupoMaximo,

        Integer edadMinima,
        Integer edadMaxima,
        Boolean incluyeKit,
        LocalDateTime fechaInicioVenta,
        LocalDateTime fechaFinVenta
) {}
