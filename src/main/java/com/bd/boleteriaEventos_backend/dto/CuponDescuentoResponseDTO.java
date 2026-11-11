package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuponDescuentoResponseDTO(
        Long idCupon,
        Long idEvento,
        String codigo,
        BigDecimal porcentajeDescuento,
        Integer usosMaximos,
        Integer usosActuales,
        LocalDateTime fechaExpiracion,
        Boolean activo
) {}
