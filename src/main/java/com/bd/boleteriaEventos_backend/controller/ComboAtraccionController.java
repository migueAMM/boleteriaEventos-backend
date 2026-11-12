package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.ComboAtraccionResponseDTO;
import com.bd.boleteriaEventos_backend.dto.ComboAtraccionWriteDTO;
import com.bd.boleteriaEventos_backend.service.ComboAtraccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/combos-atracciones")
@RequiredArgsConstructor
public class ComboAtraccionController {

    private final ComboAtraccionService comboAtraccionService;

    @PostMapping("/{idCombo}/{idAtraccion}")
    public ResponseEntity<ComboAtraccionResponseDTO> crear(@PathVariable Long idCombo, @PathVariable Long idAtraccion,
                                                           @Valid @RequestBody ComboAtraccionWriteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comboAtraccionService.crear(idCombo, idAtraccion, dto.porcentajePrecio()));
    }

    @GetMapping
    public ResponseEntity<List<ComboAtraccionResponseDTO>> listarTodos() {
        return ResponseEntity.ok(comboAtraccionService.listarTodos());
    }

    @GetMapping("/{idCombo}/{idAtraccion}")
    public ResponseEntity<ComboAtraccionResponseDTO> obtenerPorId(@PathVariable Long idCombo, @PathVariable Long idAtraccion) {
        return ResponseEntity.ok(comboAtraccionService.obtenerPorId(idCombo, idAtraccion));
    }

    @PutMapping("/{idCombo}/{idAtraccion}")
    public ResponseEntity<ComboAtraccionResponseDTO> actualizar(@PathVariable Long idCombo, @PathVariable Long idAtraccion,
                                                                @Valid @RequestBody ComboAtraccionWriteDTO dto) {
        return ResponseEntity.ok(comboAtraccionService.actualizar(idCombo, idAtraccion, dto.porcentajePrecio()));
    }

    @DeleteMapping("/{idCombo}/{idAtraccion}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idCombo, @PathVariable Long idAtraccion) {
        comboAtraccionService.eliminar(idCombo, idAtraccion);
        return ResponseEntity.noContent().build();
    }
}
