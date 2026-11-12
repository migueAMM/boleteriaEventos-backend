package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.ComboCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ComboResponseDTO;
import com.bd.boleteriaEventos_backend.service.ComboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/combos")
@RequiredArgsConstructor
public class ComboController {

    private final ComboService comboService;

    @PostMapping
    public ResponseEntity<ComboResponseDTO> crear(@Valid @RequestBody ComboCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comboService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ComboResponseDTO>> listarTodos() {
        return ResponseEntity.ok(comboService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComboResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comboService.obtenerPorId(id));
    }
}
