package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.controller.ReporteController;
import com.bd.boleteriaEventos_backend.dto.DatosReportePdf;
import com.bd.boleteriaEventos_backend.dto.ReportePdf;
import com.bd.boleteriaEventos_backend.exception.GlobalExceptionHandler;
import com.bd.boleteriaEventos_backend.repository.ReportePdfRepository;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportePdfTest {

    @Test
    void unReporteVacioConservaEncabezadosYExplicaElResultado() throws Exception {
        var repositorio = mock(ReportePdfRepository.class);
        when(repositorio.consultar(ReportePdf.AUDITORIA, null)).thenReturn(new DatosReportePdf(
                List.of("ID_RESERVA", "CLIENTE", "ATRACCION", "ID_FUNCION", "FECHA", "BOLETAS_MENORES"), List.of()));

        var lector = new PdfReader(new ReportePdfService(repositorio).generar(ReportePdf.AUDITORIA, null));
        try {
            var pagina = new PdfTextExtractor(lector).getTextFromPage(1);
            assertTrue(pagina.contains("Auditoría de menores sin adulto"));
            assertTrue(pagina.replaceAll("\\s+", " ").contains("Boletas de menores"));
            assertTrue(pagina.contains("No se encontraron registros."));
        } finally {
            lector.close();
        }
    }

    @Test
    void repiteLosEncabezadosYNumeraLasPaginas() throws Exception {
        var repositorio = mock(ReportePdfRepository.class);
        List<List<Object>> filas = IntStream.rangeClosed(1, 110)
                .mapToObj(i -> List.<Object>of("2026-11", "CONFIRMADA", i)).toList();
        when(repositorio.consultar(ReportePdf.RESERVAS_MES, null)).thenReturn(
                new DatosReportePdf(List.of("MES", "ESTADO", "TOTAL_RESERVAS"), filas));

        var lector = new PdfReader(new ReportePdfService(repositorio).generar(ReportePdf.RESERVAS_MES, null));
        try {
            var extractor = new PdfTextExtractor(lector);
            assertTrue(lector.getNumberOfPages() > 1);
            for (int pagina = 1; pagina <= lector.getNumberOfPages(); pagina++) {
                String texto = extractor.getTextFromPage(pagina);
                assertTrue(texto.contains("Total reservas"));
                assertTrue(texto.contains("Página " + pagina));
            }
            assertTrue(extractor.getTextFromPage(lector.getNumberOfPages()).contains("110"));
        } finally {
            lector.close();
        }
    }

    @Test
    void elComprobanteMuestraElTotalUnaVezYFormateaPesosColombianos() throws Exception {
        var repositorio = mock(ReportePdfRepository.class);
        var columnas = List.of("ID_RESERVA", "CLIENTE", "DOCUMENTO", "FECHA_HORA", "ESTADO_RESERVA",
                "CODIGO_BOLETA", "ATRACCION", "FECHA_FUNCION", "ZONA", "TIPO_ENTRADA", "COMBO",
                "PRECIO_PAGADO", "TIPO_DOCUMENTO", "TOTAL_RESERVA");
        var momento = LocalDateTime.of(2026, 11, 12, 10, 30);
        List<List<Object>> filas = List.of("A-001", "A-002").stream()
                .map(codigo -> List.<Object>of(7, "Lucía", "0000123", momento, "PENDIENTE", codigo,
                        "Acuario", momento, "General", "Adulto", "INDIVIDUAL", new BigDecimal("12500.25"),
                        "RESUMEN_PENDIENTE", new BigDecimal("25000.50")))
                .toList();
        when(repositorio.consultar(ReportePdf.COMPROBANTE, 7L)).thenReturn(new DatosReportePdf(columnas, filas));

        var lector = new PdfReader(new ReportePdfService(repositorio).generar(ReportePdf.COMPROBANTE, 7L));
        try {
            var texto = new PdfTextExtractor(lector).getTextFromPage(1);
            assertTrue(texto.contains("Resumen de reserva pendiente"));
            assertTrue(texto.contains("12/11/2026 10:30:00"));
            assertTrue(texto.contains("COP 12.500,25"));
            assertTrue(texto.contains("COP 25.000,50"));
            assertEquals(texto.indexOf("Total de la reserva"), texto.lastIndexOf("Total de la reserva"));
            assertTrue(texto.contains("A-001") && texto.contains("A-002"));
        } finally {
            lector.close();
        }
    }

    @Test
    void validaElParametroDelComprobanteYMantieneLasRutasJson() throws Exception {
        var pdf = mock(ReportePdfService.class);
        var json = mock(ReporteService.class);
        var api = MockMvcBuilders.standaloneSetup(new ReporteController(json, pdf))
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        api.perform(get("/reportes/3/pdf")).andExpect(status().isBadRequest());
        api.perform(get("/reportes/3/pdf").param("idReserva", "abc")).andExpect(status().isBadRequest());
        api.perform(get("/reportes/3/pdf").param("idReserva", "0")).andExpect(status().isBadRequest());
        api.perform(get("/reportes/11/pdf")).andExpect(status().isNotFound());
        api.perform(get("/reportes/aforo-por-funcion")).andExpect(status().isOk());
        api.perform(get("/reportes/ingresos-por-atraccion-y-tipo")).andExpect(status().isOk());
        api.perform(get("/reportes/combos-mas-vendidos")).andExpect(status().isOk());
        verifyNoInteractions(pdf);
        verify(json).aforoPorFuncion();
        verify(json).ingresosPorAtraccionYTipo();
        verify(json).combosMasVendidos();
    }
}
