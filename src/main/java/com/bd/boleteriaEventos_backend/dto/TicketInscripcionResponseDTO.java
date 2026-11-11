package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.EstadoTicket;
import com.bd.boleteriaEventos_backend.enums.TallaCamiseta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketInscripcionResponseDTO(
        Long idTicket,
        Long idOrden,
        Long idCategoria,
        String nombreCategoria,
        String codigoTicketQr,
        String tipoDocumentoAsistente,
        String documentoAsistente,
        String nombreCompletoAsistente,
        String emailAsistente,
        String telefonoAsistente,
        TallaCamiseta tallaCamiseta,
        String contactoEmergenciaNombre,
        String contactoEmergenciaTelefono,
        String tipoSangre,
        Integer numeroDorsal,
        BigDecimal precioUnitarioPagado,
        Boolean checkInRealizado,
        LocalDateTime fechaCheckIn,
        EstadoTicket estadoTicket
) {}
