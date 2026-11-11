package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizadorCreateDTO(
        @NotNull(message = "El id de usuario es obligatorio")
        Long idUsuario,

        @NotBlank(message = "El NIT es obligatorio")
        String nit,

        @NotBlank(message = "La razón social es obligatoria")
        String razonSocial,

        String nombreComercial,
        String descripcion,
        String sitioWeb,
        String telefonoContacto
) {}
