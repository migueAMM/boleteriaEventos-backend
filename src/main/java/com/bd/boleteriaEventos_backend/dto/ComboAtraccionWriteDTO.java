package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ComboAtraccionWriteDTO(
        @NotNull @DecimalMin("0.01") @DecimalMax("100") @Digits(integer = 3, fraction = 2)
        BigDecimal porcentajePrecio
) {}
