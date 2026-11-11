package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.TicketInscripcionResponseDTO;
import com.bd.boleteriaEventos_backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PatchMapping("/{codigoQR}/check-in")
    public ResponseEntity<TicketInscripcionResponseDTO> realizarCheckIn(@PathVariable String codigoQR) {
        return ResponseEntity.ok(ticketService.realizarCheckIn(codigoQR));
    }

    @GetMapping("/{codigoQR}")
    public ResponseEntity<TicketInscripcionResponseDTO> obtenerPorCodigoQr(@PathVariable String codigoQR) {
        return ResponseEntity.ok(ticketService.obtenerPorCodigoQr(codigoQR));
    }

    @GetMapping("/orden/{idOrden}")
    public ResponseEntity<List<TicketInscripcionResponseDTO>> listarPorOrden(@PathVariable Long idOrden) {
        return ResponseEntity.ok(ticketService.listarPorOrden(idOrden));
    }
}
