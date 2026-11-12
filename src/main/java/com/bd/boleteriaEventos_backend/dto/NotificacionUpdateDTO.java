package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificacionUpdateDTO(
        @NotBlank @Size(max = 200) String asunto,
        @NotBlank @Size(max = 1000) String mensaje
) {}
