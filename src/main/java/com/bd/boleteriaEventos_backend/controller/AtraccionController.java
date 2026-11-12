package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.AtraccionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.AtraccionResponseDTO;
import com.bd.boleteriaEventos_backend.service.AtraccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atracciones")
@RequiredArgsConstructor
public class AtraccionController {

    private final AtraccionService atraccionService;

    @PostMapping
    public ResponseEntity<AtraccionResponseDTO> crear(@Valid @RequestBody AtraccionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atraccionService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<AtraccionResponseDTO>> listarTodos() {
        return ResponseEntity.ok(atraccionService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtraccionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AtraccionCreateDTO dto) {
        return ResponseEntity.ok(atraccionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        atraccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtraccionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atraccionService.obtenerPorId(id));
    }

    @GetMapping("/prestador/{idPrestador}")
    public ResponseEntity<List<AtraccionResponseDTO>> listarPorPrestador(@PathVariable Long idPrestador) {
        return ResponseEntity.ok(atraccionService.listarPorPrestador(idPrestador));
    }
}
