package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificacionCreateDTO(
        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
        String asunto,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
        String mensaje,

        @NotNull(message = "El ID del usuario es obligatorio")
        Long idUsuario,

        Long idReserva
) {}
