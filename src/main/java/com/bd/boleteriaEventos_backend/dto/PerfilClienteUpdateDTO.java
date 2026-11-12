package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilClienteUpdateDTO(
        @NotBlank @Size(max = 30) String documento,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 30) String telefono
) {}
