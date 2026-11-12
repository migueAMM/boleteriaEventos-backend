package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ComboResponseDTO(
        Long idCombo,
        String nombre,
        BigDecimal precio,
        Integer diasVigencia,
        String estado,
        List<Long> idsAtracciones
) {}
