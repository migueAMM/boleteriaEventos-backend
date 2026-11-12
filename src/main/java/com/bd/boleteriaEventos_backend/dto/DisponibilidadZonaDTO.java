package com.bd.boleteriaEventos_backend.dto;

public record DisponibilidadZonaDTO(Long idZona, String nombre, int aforoTotal,
                                    long ocupadas, long disponibles) {}
