package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;

public record TipoEntradaResponseDTO(
        Long idTipoEntrada,
        String nombre,
        BigDecimal porcentajeDescuento,
        Boolean requiereAdulto,
        boolean esAdulto
) {}
