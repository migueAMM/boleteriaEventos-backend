package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagoTransaccionCreateDTO(
        @NotNull(message = "El id de la orden es obligatorio")
        Long idOrden,

        @NotBlank(message = "La pasarela de pago es obligatoria")
        String pasarelaPago,

        @NotBlank(message = "La referencia externa es obligatoria")
        String referenciaExterna,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto
) {}
