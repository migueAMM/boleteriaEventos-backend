package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PerfilClienteCreateDTO(
        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 30, message = "El documento no puede exceder 30 caracteres")
        String documento,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,

        @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
        String telefono,

        @NotNull(message = "El ID del usuario del sistema es obligatorio")
        Long idUsuario
) {}
