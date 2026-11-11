package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.EstadoEvento;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;

import java.time.LocalDateTime;
import java.util.List;

public record EventoResponseDTO(
        Long idEvento,
        Long idOrganizador,
        String nombreOrganizador,
        String titulo,
        String slug,
        String descripcion,
        TipoEvento tipoEvento,
        String ciudad,
        String direccion,
        String lugarNombre,
        LocalDateTime fechaInicioEvento,
        LocalDateTime fechaFinEvento,
        LocalDateTime fechaAperturaInscripciones,
        LocalDateTime fechaCierreInscripciones,
        String imagenBannerUrl,
        Integer aforoMaximoTotal,
        EstadoEvento estado,
        List<CategoriaEventoResponseDTO> categorias
) {}
