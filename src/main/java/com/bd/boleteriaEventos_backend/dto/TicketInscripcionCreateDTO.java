package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.TallaCamiseta;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TicketInscripcionCreateDTO(
        @NotNull(message = "El id de categoría es obligatorio")
        Long idCategoria,

        @NotBlank(message = "El tipo de documento del asistente es obligatorio")
        String tipoDocumentoAsistente,

        @NotBlank(message = "El documento del asistente es obligatorio")
        String documentoAsistente,

        @NotBlank(message = "El nombre completo del asistente es obligatorio")
        String nombreCompletoAsistente,

        @NotBlank(message = "El email del asistente es obligatorio")
        @Email(message = "El email del asistente debe tener un formato válido")
        String emailAsistente,

        String telefonoAsistente,
        LocalDate fechaNacimientoAsistente,
        TallaCamiseta tallaCamiseta,
        String contactoEmergenciaNombre,
        String contactoEmergenciaTelefono,
        String tipoSangre
) {}
