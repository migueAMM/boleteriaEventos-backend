package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BoletaCreateDTO(
        String codigo,
        String estado,
        BigDecimal precioPagado,
        @NotNull Long idReserva,
        @NotNull Long idZona,
        @NotNull Long idTipoEntrada,
        Long idCombo
) {}
