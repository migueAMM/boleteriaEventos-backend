package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;

public record ZonaResponseDTO(
        Long idZona,
        String nombre,
        Integer aforo,
        BigDecimal precio,
        Long idFuncion
) {}
