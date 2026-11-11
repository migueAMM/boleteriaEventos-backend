package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.PagoTransaccionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PagoTransaccionResponseDTO;
import com.bd.boleteriaEventos_backend.service.PagoTransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoTransaccionController {

    private final PagoTransaccionService pagoTransaccionService;

    @PostMapping
    public ResponseEntity<PagoTransaccionResponseDTO> registrarPago(@Valid @RequestBody PagoTransaccionCreateDTO dto) {
        PagoTransaccionResponseDTO response = pagoTransaccionService.registrarPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{idPago}/confirmar")
    public ResponseEntity<PagoTransaccionResponseDTO> confirmarPago(@PathVariable Long idPago) {
        return ResponseEntity.ok(pagoTransaccionService.confirmarPago(idPago));
    }

    @GetMapping("/orden/{idOrden}")
    public ResponseEntity<List<PagoTransaccionResponseDTO>> listarPorOrden(@PathVariable Long idOrden) {
        return ResponseEntity.ok(pagoTransaccionService.listarPorOrden(idOrden));
    }
}
