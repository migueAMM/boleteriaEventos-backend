package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrdenCompraCreateDTO(
        @NotNull(message = "El id de comprador es obligatorio")
        Long idComprador,

        @NotNull(message = "El id de evento es obligatorio")
        Long idEvento,

        String codigoCupon,

        @NotEmpty(message = "Debe incluir al menos un ticket en la orden")
        @Valid
        List<TicketInscripcionCreateDTO> tickets
) {}
