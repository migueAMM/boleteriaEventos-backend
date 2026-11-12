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

    @GetMapping("/{id}")
    public ResponseEntity<AtraccionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atraccionService.obtenerPorId(id));
    }

    @GetMapping("/prestador/{idPrestador}")
    public ResponseEntity<List<AtraccionResponseDTO>> listarPorPrestador(@PathVariable Long idPrestador) {
        return ResponseEntity.ok(atraccionService.listarPorPrestador(idPrestador));
    }
}
