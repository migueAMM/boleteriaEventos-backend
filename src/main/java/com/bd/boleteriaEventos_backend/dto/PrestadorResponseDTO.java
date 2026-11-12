package com.bd.boleteriaEventos_backend.dto;

public record PrestadorResponseDTO(
        Long idPrestador,
        String nit,
        String nombre,
        String correo,
        Long idMunicipio,
        String nombreMunicipio,
        Long idTipoPrestador,
        String nombreTipoPrestador
) {}
