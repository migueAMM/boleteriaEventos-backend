package com.bd.boleteriaEventos_backend.dto;

public record PerfilClienteResponseDTO(
        Long idPerfil,
        String documento,
        String nombre,
        String telefono,
        Long idUsuario,
        String correoUsuario
) {}
