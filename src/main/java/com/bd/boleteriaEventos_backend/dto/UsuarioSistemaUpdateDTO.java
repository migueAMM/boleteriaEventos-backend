package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioSistemaUpdateDTO(
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Size(max = 30) String estado,
        @NotNull Long idRol
) {}
