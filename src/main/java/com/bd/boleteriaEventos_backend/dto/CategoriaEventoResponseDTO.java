package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.EstadoCategoria;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CategoriaEventoResponseDTO(
        Long idCategoria,
        Long idEvento,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        BigDecimal cargoServicio,
        Integer cupoMaximo,
        Integer cuposDisponibles,
        Integer edadMinima,
        Integer edadMaxima,
        Boolean incluyeKit,
        LocalDateTime fechaInicioVenta,
        LocalDateTime fechaFinVenta,
        EstadoCategoria estado
) {}
