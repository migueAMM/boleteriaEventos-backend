package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.FuncionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.FuncionResponseDTO;
import com.bd.boleteriaEventos_backend.service.FuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funciones")
@RequiredArgsConstructor
public class FuncionController {

    private final FuncionService funcionService;

    @PostMapping
    public ResponseEntity<FuncionResponseDTO> crear(@Valid @RequestBody FuncionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<FuncionResponseDTO>> listarTodos() {
        return ResponseEntity.ok(funcionService.listarTodos());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<FuncionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(funcionService.cancelar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(funcionService.obtenerPorId(id));
    }

    @GetMapping("/atraccion/{idAtraccion}")
    public ResponseEntity<List<FuncionResponseDTO>> listarPorAtraccion(@PathVariable Long idAtraccion) {
        return ResponseEntity.ok(funcionService.listarPorAtraccion(idAtraccion));
    }
}
