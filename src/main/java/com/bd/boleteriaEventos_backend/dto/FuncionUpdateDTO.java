package com.bd.boleteriaEventos_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record FuncionUpdateDTO(
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
        @NotNull @JsonFormat(pattern = "HH:mm[:ss]") LocalTime horaApertura,
        @NotNull @JsonFormat(pattern = "HH:mm[:ss]") LocalTime horaCierre,
        @NotNull Long idAtraccion
) {}
