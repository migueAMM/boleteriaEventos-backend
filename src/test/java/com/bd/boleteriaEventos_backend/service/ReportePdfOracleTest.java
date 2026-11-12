package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.controller.ReporteController;
import com.bd.boleteriaEventos_backend.dto.ReportePdf;
import com.bd.boleteriaEventos_backend.repository.ReportePdfRepository;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "BOLETERIA_PRUEBA_ORACLE", matches = "true")
class ReportePdfOracleTest {

    @Autowired private ReporteController controlador;
    @Autowired private ReportePdfRepository repositorio;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void losDiezEndpointsDescarganPdfLegiblesConLosResultadosDeOracle() throws Exception {
        var archivo = new ClassPathResource("reportes/05_reportes.sql").getContentAsString(StandardCharsets.UTF_8);
        var sqlOriginal = Arrays.stream(archivo.replaceAll("(?m)^\\h*--[^\\r\\n]*", "").split("(?m);\\h*$"))
                .map(String::strip).filter(sql -> !sql.isEmpty()).toList();
        assertEquals(10, sqlOriginal.size());
        var mvc = MockMvcBuilders.standaloneSetup(controlador).build();

        Long reserva = jdbc.queryForObject(
                "select min(ID_RESERVA) from (" + sqlOriginal.get(2) + ")", Long.class);

        for (ReportePdf reporte : ReportePdf.values()) {
            Long idReserva = null;
            String consulta = sqlOriginal.get(reporte.numero() - 1);
            var solicitud = get("/reportes/{numero}/pdf", reporte.numero());
            String archivoEsperado = reporte.nombreArchivo();
            Object[] parametros = {};
            if (reporte == ReportePdf.COMPROBANTE) {
                idReserva = reserva == null ? Long.MAX_VALUE : reserva;
                solicitud.param("idReserva", idReserva.toString());
                archivoEsperado += "-" + idReserva;
                consulta = "select * from (" + consulta + ") where ID_RESERVA = ?";
                parametros = new Object[]{idReserva};
            }
            var esperado = jdbc.queryForList(consulta, parametros);
            var obtenido = repositorio.consultar(reporte, idReserva);
            assertEquals(esperado.size(), obtenido.filas().size(), reporte.titulo());
            System.out.printf("Reporte %d: %d filas de Oracle%n", reporte.numero(), esperado.size());
            for (int fila = 0; fila < esperado.size(); fila++) {
                for (int columna = 0; columna < obtenido.columnas().size(); columna++) {
                    var original = esperado.get(fila).get(obtenido.columnas().get(columna));
                    var valor = obtenido.filas().get(fila).get(columna);
                    if (original == null || original instanceof Number || original instanceof String) {
                        assertEquals(original, valor, reporte.titulo() + " / " + obtenido.columnas().get(columna));
                    }
                }
            }

            byte[] pdf = mvc.perform(solicitud).andExpect(status().isOk())
                    .andExpect(content().contentType("application/pdf"))
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + archivoEsperado + ".pdf\""))
                    .andReturn().getResponse().getContentAsByteArray();

            var lector = new PdfReader(pdf);
            try {
                var texto = new PdfTextExtractor(lector).getTextFromPage(1);
                assertTrue(texto.contains("Sistema de Boletería"), reporte.titulo());
                assertTrue(texto.contains("Generado el"), reporte.titulo());
                if (esperado.isEmpty()) {
                    assertTrue(texto.contains("No se encontraron registros."), reporte.titulo());
                }
            } finally {
                lector.close();
            }

            if ("true".equals(System.getenv("BOLETERIA_GUARDAR_PDF_PRUEBA"))) {
                Path carpeta = Files.createDirectories(Path.of("build", "pdf-reportes-prueba"));
                Files.write(carpeta.resolve(reporte.numero() + "-" + archivoEsperado + ".pdf"), pdf);
            }
        }
    }
}
