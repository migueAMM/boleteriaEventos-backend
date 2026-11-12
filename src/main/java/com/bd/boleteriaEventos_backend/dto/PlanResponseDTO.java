package com.bd.boleteriaEventos_backend.dto;

public record PlanResponseDTO(
        Long idPlan,
        String nombre,
        String descripcion,
        String estado,
        Long idPrestador,
        String nombrePrestador
) {}
