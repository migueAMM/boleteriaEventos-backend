package com.bd.boleteriaEventos_backend.dto;

import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long idReserva,
        LocalDateTime fechaHora,
        String estado,
        Long idPerfil,
        String nombreCliente,
        Long idPlan,
        String nombrePlan
) {}
