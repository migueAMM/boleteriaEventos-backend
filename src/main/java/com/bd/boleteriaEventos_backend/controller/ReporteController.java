package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.AforoFuncionDTO;
import com.bd.boleteriaEventos_backend.dto.ComboVentasDTO;
import com.bd.boleteriaEventos_backend.dto.IngresosAtraccionDTO;
import com.bd.boleteriaEventos_backend.dto.ReportePdf;
import com.bd.boleteriaEventos_backend.service.ReportePdfService;
import com.bd.boleteriaEventos_backend.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService reportePdfService;

    @GetMapping("/{numero}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable int numero,
                                                @RequestParam(required = false) String idReserva) {
        ReportePdf reporte = ReportePdf.buscar(numero);
        Long reserva = null;
        String archivo = reporte.nombreArchivo();
        if (reporte == ReportePdf.COMPROBANTE) {
            try {
                reserva = Long.valueOf(idReserva);
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().build();
            }
            if (reserva <= 0) {
                return ResponseEntity.badRequest().build();
            }
            archivo += "-" + reserva;
        }

        byte[] pdf = reportePdfService.generar(reporte, reserva);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo + ".pdf").build().toString())
                .body(pdf);
    }

    @GetMapping("/aforo-por-funcion")
    public List<AforoFuncionDTO> aforoPorFuncion() {
        return reporteService.aforoPorFuncion();
    }

    @GetMapping("/ingresos-por-atraccion-y-tipo")
    public List<IngresosAtraccionDTO> ingresosPorAtraccionYTipo() {
        return reporteService.ingresosPorAtraccionYTipo();
    }

    @GetMapping("/combos-mas-vendidos")
    public List<ComboVentasDTO> combosMasVendidos() {
        return reporteService.combosMasVendidos();
    }
}
