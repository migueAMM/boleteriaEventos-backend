package com.bd.boleteriaEventos_backend.dto;

import java.util.List;

public record DatosReportePdf(List<String> columnas, List<List<Object>> filas) {}
