package com.bd.boleteriaEventos_backend.dto;

public record OrganizadorResponseDTO(
        Long idOrganizador,
        Long idUsuario,
        String nombreUsuario,
        String nit,
        String razonSocial,
        String nombreComercial,
        String descripcion,
        String sitioWeb,
        String telefonoContacto,
        Boolean verificado
) {}
