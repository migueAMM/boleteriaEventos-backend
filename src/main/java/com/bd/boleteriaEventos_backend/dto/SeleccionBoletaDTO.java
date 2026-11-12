package com.bd.boleteriaEventos_backend.dto;

import jakarta.validation.constraints.NotNull;

public record SeleccionBoletaDTO(@NotNull Long idZona, @NotNull Long idTipoEntrada) {}
