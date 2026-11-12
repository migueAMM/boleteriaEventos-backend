package com.bd.boleteriaEventos_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record FuncionCreateDTO(
        @NotNull(message = "La fecha es obligatoria")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fecha,

        @NotNull(message = "La hora de apertura es obligatoria")
        @JsonFormat(pattern = "HH:mm[:ss]")
        LocalTime horaApertura,

        @NotNull(message = "La hora de cierre es obligatoria")
        @JsonFormat(pattern = "HH:mm[:ss]")
        LocalTime horaCierre,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede exceder 30 caracteres")
        String estado,

        @NotNull(message = "El ID de la atracción es obligatorio")
        Long idAtraccion
) {}
