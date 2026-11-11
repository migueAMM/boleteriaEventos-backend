package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.Genero;
import com.bd.boleteriaEventos_backend.enums.Rol;
import com.bd.boleteriaEventos_backend.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UsuarioCreateDTO(
        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,

        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        String telefono,

        LocalDate fechaNacimiento,

        Genero genero,

        Rol rol
) {}
