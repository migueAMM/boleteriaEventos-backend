package com.bd.boleteriaEventos_backend.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;

public record AforoFuncionDTO(Long idFuncion, String atraccion, LocalDate fecha,
                              LocalTime horaApertura, LocalTime horaCierre, long aforoTotal,
                              long ocupadas, long disponibles, BigDecimal porcentajeOcupacion) {
    public AforoFuncionDTO(Long idFuncion, String atraccion, LocalDate fecha, LocalTime horaApertura,
                           LocalTime horaCierre, Long aforoTotal, Long ocupadas) {
        this(idFuncion, atraccion, fecha, horaApertura, horaCierre, aforoTotal, ocupadas,
                Math.max(0, aforoTotal - ocupadas),
                aforoTotal == 0 ? BigDecimal.ZERO
                        : BigDecimal.valueOf(ocupadas).movePointRight(2)
                                .divide(BigDecimal.valueOf(aforoTotal), 2, RoundingMode.HALF_UP));
    }
}
