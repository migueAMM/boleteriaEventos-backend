package com.bd.boleteriaEventos_backend.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Converter
public class HoraFuncionConverter implements AttributeConverter<LocalTime, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(LocalTime hora) {
        return hora == null ? null : BigDecimal.valueOf(hora.toNanoOfDay(), 9);
    }

    @Override
    public LocalTime convertToEntityAttribute(BigDecimal segundos) {
        return segundos == null ? null : LocalTime.ofNanoOfDay(segundos.movePointRight(9).longValueExact());
    }
}
