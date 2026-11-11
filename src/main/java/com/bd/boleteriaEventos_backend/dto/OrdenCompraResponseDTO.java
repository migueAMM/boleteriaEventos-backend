package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.EstadoOrden;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenCompraResponseDTO(
        Long idOrden,
        String codigoOrden,
        Long idComprador,
        String nombreComprador,
        Long idEvento,
        String tituloEvento,
        String codigoCupon,
        BigDecimal subtotal,
        BigDecimal totalCargosServicio,
        BigDecimal totalDescuento,
        BigDecimal montoTotal,
        EstadoOrden estadoOrden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaExpiracionReserva,
        List<TicketInscripcionResponseDTO> tickets
) {}
