package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.BoletaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.BoletaResponseDTO;
import com.bd.boleteriaEventos_backend.service.BoletaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    public ResponseEntity<BoletaResponseDTO> crear(@Valid @RequestBody BoletaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boletaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<BoletaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(boletaService.listarTodos());
    }

    @PatchMapping("/codigo/{codigo}/usar")
    public ResponseEntity<BoletaResponseDTO> usar(@PathVariable String codigo) {
        return ResponseEntity.ok(boletaService.usar(codigo));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<BoletaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.anular(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> actualizar(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.actualizar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boletaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.obtenerPorId(id));
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<BoletaResponseDTO>> listarPorReserva(@PathVariable Long idReserva) {
        return ResponseEntity.ok(boletaService.listarPorReserva(idReserva));
    }
}
