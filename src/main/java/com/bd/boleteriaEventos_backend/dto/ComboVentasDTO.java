package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;

public record ComboVentasDTO(Long idCombo, String nombre, Long compras, BigDecimal ingresos) {}
