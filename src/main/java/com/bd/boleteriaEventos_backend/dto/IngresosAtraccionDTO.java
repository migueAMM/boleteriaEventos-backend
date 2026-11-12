package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;

public record IngresosAtraccionDTO(Long idAtraccion, String atraccion, Long idTipoEntrada,
                                  String tipoEntrada, Long boletasVendidas, BigDecimal ingresos) {}
