package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ComboUpdateDTO(
        @NotBlank @Size(max = 150) String nombre,
        @NotNull @DecimalMin("0") BigDecimal precio,
        @NotNull @Min(1) Integer diasVigencia,
        @NotBlank @Size(max = 30) String estado,
        @Size(min = 1, max = 10000) List<@NotNull @Valid AtraccionReparto> atracciones
) {
    public record AtraccionReparto(
            @NotNull Long idAtraccion,
            @NotNull @DecimalMin("0.01") @DecimalMax("100") @Digits(integer = 3, fraction = 2)
            BigDecimal porcentajePrecio
    ) {}
}
