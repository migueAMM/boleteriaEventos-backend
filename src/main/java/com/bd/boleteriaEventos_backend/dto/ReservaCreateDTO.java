package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservaCreateDTO(
        @NotNull Long idPerfil,
        Long idPlan,
        String estado,
        List<@NotNull @Valid SeleccionBoletaDTO> boletas,
        Long idCombo
) {}
