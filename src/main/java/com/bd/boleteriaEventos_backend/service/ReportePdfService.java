package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.DatosReportePdf;
import com.bd.boleteriaEventos_backend.dto.ReportePdf;
import com.bd.boleteriaEventos_backend.repository.ReportePdfRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReportePdfService {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final Pattern HORA_ORACLE = Pattern.compile("(?:\\+?\\d+\\s+)?(\\d{1,2}:\\d{2}:\\d{2})(?:\\.\\d+)?");
    private static final Set<String> MONEDA = Set.of("PRECIO", "PRECIO_PAGADO", "TOTAL_RESERVA", "TOTAL_INGRESOS", "PRECIO_PROMEDIO");
    private static final Color TEXTO = new Color(28, 39, 49);
    private static final Color GRIS = new Color(80, 94, 107);
    private static final Color LINEA = new Color(214, 221, 227);
    private static final Color FONDO_ENCABEZADO = new Color(231, 237, 242);
    private static final Color FILA_ALTERNA = new Color(248, 250, 252);

    private final ReportePdfRepository repository;

    @Transactional(readOnly = true)
    public byte[] generar(ReportePdf reporte, Long idReserva) {
        DatosReportePdf datos = repository.consultar(reporte, idReserva);
        return escribir(reporte, datos);
    }

    private byte[] escribir(ReportePdf reporte, DatosReportePdf datos) {
        boolean comprobante = reporte == ReportePdf.COMPROBANTE;
        int desde = comprobante ? 5 : 0;
        int hasta = comprobante ? 12 : datos.columnas().size();
        if (hasta - desde != reporte.encabezados().size() || (comprobante && datos.columnas().size() != 14)) {
            throw new IllegalStateException("Las columnas del reporte no coinciden con la consulta");
        }

        var salida = new ByteArrayOutputStream();
        var pagina = reporte.encabezados().size() >= 7 ? PageSize.A4.rotate() : PageSize.A4;
        var documento = new Document(pagina, 32, 32, 34, 42);
        try {
            PdfWriter writer = PdfWriter.getInstance(documento, salida);
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter pdf, Document paginaActual) {
                    ColumnText.showTextAligned(pdf.getDirectContent(), Element.ALIGN_RIGHT,
                            new Phrase("Página " + pdf.getPageNumber(), fuente(8, Font.NORMAL, GRIS)),
                            paginaActual.right(), 23, 0);
                }
            });
            documento.addTitle(reporte.titulo());
            documento.addCreator("Sistema de Boletería");
            documento.open();
            documento.add(new Paragraph("Sistema de Boletería", fuente(11, Font.BOLD, GRIS)));
            String titulo = reporte.titulo();
            if (comprobante && !datos.filas().isEmpty()) {
                titulo = "COMPROBANTE_COMPRA".equals(datos.filas().getFirst().get(12))
                        ? "Comprobante de compra" : "Resumen de reserva pendiente";
            }
            documento.add(new Paragraph(titulo, fuente(20, Font.BOLD, TEXTO)));
            Paragraph fecha = new Paragraph("Generado el " + FECHA_HORA.format(LocalDateTime.now(BOGOTA)) + " (Bogotá)",
                    fuente(9, Font.NORMAL, GRIS));
            fecha.setSpacingAfter(13);
            documento.add(fecha);
            if (comprobante && !datos.filas().isEmpty()) {
                agregarResumen(documento, datos);
            }
            documento.add(tabla(reporte.encabezados(), datos, desde, hasta));
        } catch (DocumentException ex) {
            throw new IllegalStateException("No se pudo generar el PDF del reporte " + reporte.numero(), ex);
        } finally {
            documento.close();
        }
        return salida.toByteArray();
    }

    private void agregarResumen(Document documento, DatosReportePdf datos) throws DocumentException {
        List<Object> fila = datos.filas().getFirst();
        documento.add(new Paragraph("Reserva " + fila.get(0) + "  |  " + fila.get(4)
                + "  |  " + formatear(datos.columnas().get(3), fila.get(3)), fuente(10, Font.BOLD, TEXTO)));
        documento.add(new Paragraph("Cliente " + fila.get(1) + "  |  Documento " + fila.get(2),
                fuente(10, Font.NORMAL, TEXTO)));
        Paragraph total = new Paragraph("Total de la reserva  " + formatear("TOTAL_RESERVA", fila.get(13)),
                fuente(11, Font.BOLD, TEXTO));
        total.setSpacingAfter(13);
        documento.add(total);
    }

    private PdfPTable tabla(List<String> encabezados, DatosReportePdf datos, int desde, int hasta) throws DocumentException {
        var tabla = new PdfPTable(encabezados.size());
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSplitRows(true);
        tabla.setSplitLate(false);
        float[] anchos = new float[encabezados.size()];
        for (int i = desde; i < hasta; i++) {
            String columna = datos.columnas().get(i);
            anchos[i - desde] = ancho(columna);
            tabla.addCell(celda(encabezados.get(i - desde), true, false, FONDO_ENCABEZADO));
        }
        tabla.setWidths(anchos);
        if (datos.filas().isEmpty()) {
            PdfPCell vacia = celda("No se encontraron registros.", false, false, Color.WHITE);
            vacia.setColspan(encabezados.size());
            tabla.addCell(vacia);
        }

        for (int fila = 0; fila < datos.filas().size(); fila++) {
            List<Object> valores = datos.filas().get(fila);
            Color fondo = fila % 2 == 0 ? Color.WHITE : FILA_ALTERNA;
            for (int i = desde; i < hasta; i++) {
                Object valor = valores.get(i);
                tabla.addCell(celda(formatear(datos.columnas().get(i), valor), false, valor instanceof Number, fondo));
            }
        }
        return tabla;
    }

    private float ancho(String columna) {
        if (columna.startsWith("ID_")) {
            return 0.68f;
        }
        return switch (columna) {
            case "ATRACCION", "PRESTADOR", "CLIENTE", "CORREO", "CODIGO_BOLETA", "PLAN_TURISTICO",
                    "TIPO_ENTRADA", "COMBO", "TIPO_PRESTADOR" -> 1.55f;
            case "FECHA_HORA", "PRIMERA_RESERVA", "ULTIMA_RESERVA", "TOTAL_INGRESOS", "PRECIO_PROMEDIO",
                    "PRECIO_PAGADO", "PRECIO" -> 1.35f;
            default -> 1;
        };
    }

    private PdfPCell celda(String texto, boolean encabezado, boolean numero, Color fondo) {
        var celda = new PdfPCell(new Phrase(texto, fuente(9, encabezado ? Font.BOLD : Font.NORMAL, TEXTO)));
        celda.setPadding(6);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setHorizontalAlignment(numero ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
        celda.setBorderColor(LINEA);
        celda.setBorderWidth(0.45f);
        celda.setBackgroundColor(fondo);
        return celda;
    }

    private String formatear(String columna, Object valor) {
        if (valor == null) {
            return "-";
        }
        if (valor instanceof LocalDateTime fecha) {
            return ("FECHA".equals(columna) || "FECHA_FUNCION".equals(columna) ? FECHA : FECHA_HORA).format(fecha);
        }
        if (valor instanceof Number numero && !columna.startsWith("ID_")) {
            boolean moneda = MONEDA.contains(columna);
            boolean porcentaje = "PORCENTAJE_OCUPACION".equals(columna);
            NumberFormat formato = NumberFormat.getNumberInstance(COLOMBIA);
            formato.setMinimumFractionDigits(moneda || porcentaje ? 2 : 0);
            formato.setMaximumFractionDigits(moneda || porcentaje ? 2 : 0);
            return (moneda ? "COP " : "") + formato.format(numero) + (porcentaje ? " %" : "");
        }
        String texto = valor.toString();
        if ("HORA_APERTURA".equals(columna) || "HORA_CIERRE".equals(columna)) {
            var hora = HORA_ORACLE.matcher(texto.strip());
            if (hora.matches()) {
                return hora.group(1);
            }
        }
        return texto;
    }

    private Font fuente(float tamano, int estilo, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, tamano, estilo, color);
    }
}
