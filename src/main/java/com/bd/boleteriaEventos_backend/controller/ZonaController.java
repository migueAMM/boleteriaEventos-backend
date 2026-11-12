package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.ZonaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ZonaResponseDTO;
import com.bd.boleteriaEventos_backend.service.ZonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final ZonaService zonaService;

    @PostMapping
    public ResponseEntity<ZonaResponseDTO> crear(@Valid @RequestBody ZonaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zonaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ZonaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(zonaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZonaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.obtenerPorId(id));
    }

    @GetMapping("/funcion/{idFuncion}")
    public ResponseEntity<List<ZonaResponseDTO>> listarPorFuncion(@PathVariable Long idFuncion) {
        return ResponseEntity.ok(zonaService.listarPorFuncion(idFuncion));
    }
}
