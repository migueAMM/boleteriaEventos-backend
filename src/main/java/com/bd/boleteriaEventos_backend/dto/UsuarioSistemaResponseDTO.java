package com.bd.boleteriaEventos_backend.dto;

public record UsuarioSistemaResponseDTO(
        Long idUsuario,
        String correo,
        String estado,
        Long idRol,
        String nombreRol
) {}
