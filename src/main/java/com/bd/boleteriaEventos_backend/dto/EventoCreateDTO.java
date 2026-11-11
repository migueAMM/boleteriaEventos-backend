package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventoCreateDTO(
        @NotNull(message = "El id de organizador es obligatorio")
        Long idOrganizador,

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        @NotNull(message = "El tipo de evento es obligatorio")
        TipoEvento tipoEvento,

        String ciudad,
        String direccion,
        String lugarNombre,
        LocalDateTime fechaInicioEvento,
        LocalDateTime fechaFinEvento,
        LocalDateTime fechaAperturaInscripciones,
        LocalDateTime fechaCierreInscripciones,
        String imagenBannerUrl,
        Integer aforoMaximoTotal
) {}
