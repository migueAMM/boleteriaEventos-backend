package com.bd.boleteriaEventos_backend.dto;

import java.util.List;

public record DisponibilidadFuncionDTO(Long idFuncion, List<DisponibilidadZonaDTO> zonas) {}
