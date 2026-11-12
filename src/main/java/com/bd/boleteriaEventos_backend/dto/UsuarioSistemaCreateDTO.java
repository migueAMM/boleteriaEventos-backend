package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioSistemaCreateDTO(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato de correo no es válido")
        @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
        String correo,

        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 6, max = 100, message = "La clave debe tener entre 6 y 100 caracteres")
        String clave,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede exceder 30 caracteres")
        String estado,

        @NotNull(message = "El ID del rol es obligatorio")
        Long idRol
) {}
