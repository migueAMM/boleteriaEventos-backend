package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MunicipioCreateDTO(
        @NotBlank(message = "El nombre del municipio es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @NotBlank(message = "El departamento es obligatorio")
        @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
        String departamento
) {}
