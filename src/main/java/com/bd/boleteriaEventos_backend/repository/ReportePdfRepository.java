package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.dto.DatosReportePdf;
import com.bd.boleteriaEventos_backend.dto.ReportePdf;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class ReportePdfRepository {

    private static final String ORDEN_COMPROBANTE = "ORDER BY R.ID_RESERVA, B.CODIGO";

    private final JdbcTemplate jdbcTemplate;
    private final List<String> consultas;

    public ReportePdfRepository(JdbcTemplate jdbcTemplate) throws IOException {
        this.jdbcTemplate = jdbcTemplate;
        String archivo = new ClassPathResource("reportes/05_reportes.sql").getContentAsString(StandardCharsets.UTF_8);
        consultas = Arrays.stream(archivo.replaceAll("(?m)^\\h*--[^\\r\\n]*", "").split("(?m);\\h*$"))
                .map(String::strip)
                .filter(consulta -> !consulta.isEmpty())
                .toList();
        if (consultas.size() != ReportePdf.values().length
                || consultas.stream().anyMatch(consulta -> !consulta.startsWith("SELECT"))
                || !consultas.get(ReportePdf.COMPROBANTE.numero() - 1).endsWith(ORDEN_COMPROBANTE)) {
            throw new IllegalStateException("El archivo de reportes no contiene las diez consultas esperadas");
        }
    }

    public DatosReportePdf consultar(ReportePdf reporte, Long idReserva) {
        String consulta = consultas.get(reporte.numero() - 1);
        if (reporte == ReportePdf.COMPROBANTE) {
            // El filtro se aplica por la clave de la partición del total analítico.
            String filtrada = consulta.replace(ORDEN_COMPROBANTE, "AND R.ID_RESERVA = ?\n" + ORDEN_COMPROBANTE);
            return jdbcTemplate.query(filtrada, this::leerTabla, idReserva);
        }
        return jdbcTemplate.query(consulta, this::leerTabla);
    }

    private DatosReportePdf leerTabla(ResultSet resultado) throws SQLException {
        var meta = resultado.getMetaData();
        List<String> columnas = new ArrayList<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columnas.add(meta.getColumnLabel(i));
        }

        List<List<Object>> filas = new ArrayList<>();
        while (resultado.next()) {
            List<Object> fila = new ArrayList<>(columnas.size());
            for (int i = 1; i <= columnas.size(); i++) {
                fila.add(leerValor(resultado, i, meta.getColumnType(i), columnas.get(i - 1)));
            }
            filas.add(fila);
        }
        return new DatosReportePdf(columnas, filas);
    }

    private Object leerValor(ResultSet resultado, int indice, int tipo, String columna) throws SQLException {
        if ("HORA_APERTURA".equals(columna) || "HORA_CIERRE".equals(columna)) {
            return resultado.getString(indice);
        }
        if (tipo == Types.DATE || tipo == Types.TIMESTAMP) {
            var valor = resultado.getTimestamp(indice);
            return valor == null ? null : valor.toLocalDateTime();
        }
        return resultado.getObject(indice);
    }
}
