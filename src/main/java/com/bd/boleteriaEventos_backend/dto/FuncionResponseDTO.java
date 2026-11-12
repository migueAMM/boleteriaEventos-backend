package com.bd.boleteriaEventos_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FuncionResponseDTO(
        Long idFuncion,
        LocalDate fecha,
        LocalTime horaApertura,
        LocalTime horaCierre,
        String estado,
        Long idAtraccion,
        String nombreAtraccion
) {}
