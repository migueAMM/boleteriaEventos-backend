package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.EstadoPago;
import com.bd.boleteriaEventos_backend.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoTransaccionResponseDTO(
        Long idPago,
        Long idOrden,
        String codigoOrden,
        String pasarelaPago,
        String referenciaExterna,
        MetodoPago metodoPago,
        BigDecimal monto,
        EstadoPago estadoPago,
        LocalDateTime fechaPago
) {}
