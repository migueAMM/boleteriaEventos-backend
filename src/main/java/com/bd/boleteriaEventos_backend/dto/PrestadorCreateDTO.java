package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PrestadorCreateDTO(
        @NotBlank(message = "El NIT es obligatorio")
        @Size(max = 30, message = "El NIT no puede exceder 30 caracteres")
        String nit,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato de correo no es válido")
        @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
        String correo,

        @NotNull(message = "El ID del municipio es obligatorio")
        Long idMunicipio,

        @NotNull(message = "El ID del tipo de prestador es obligatorio")
        Long idTipoPrestador
) {}
