package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;

public record BoletaResponseDTO(
        Long idBoleta,
        String codigo,
        String estado,
        BigDecimal precioPagado,
        Long idReserva,
        Long idZona,
        String nombreZona,
        Long idTipoEntrada,
        String nombreTipoEntrada,
        Long idCombo,
        String nombreCombo
) {}
