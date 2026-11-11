package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.Genero;
import com.bd.boleteriaEventos_backend.enums.Rol;
import com.bd.boleteriaEventos_backend.enums.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long idUsuario,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        LocalDate fechaNacimiento,
        Genero genero,
        Rol rol,
        LocalDateTime fechaRegistro,
        Boolean activo
) {}
