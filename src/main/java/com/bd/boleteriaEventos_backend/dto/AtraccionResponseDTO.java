package com.bd.boleteriaEventos_backend.dto;

public record AtraccionResponseDTO(
        Long idAtraccion,
        String nombre,
        String descripcion,
        Long idPrestador,
        String nombrePrestador
) {}
