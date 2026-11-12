package com.bd.boleteriaEventos_backend.dto;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
        Long idNotificacion,
        String asunto,
        String mensaje,
        LocalDateTime fechaHora,
        Boolean leida,
        Long idUsuario,
        Long idReserva
) {}
