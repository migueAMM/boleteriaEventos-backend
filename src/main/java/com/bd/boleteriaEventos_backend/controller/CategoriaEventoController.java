package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.CategoriaEventoCreateDTO;
import com.bd.boleteriaEventos_backend.dto.CategoriaEventoResponseDTO;
import com.bd.boleteriaEventos_backend.service.CategoriaEventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-evento")
@RequiredArgsConstructor
public class CategoriaEventoController {

    private final CategoriaEventoService categoriaEventoService;

    @PostMapping
    public ResponseEntity<CategoriaEventoResponseDTO> crear(@Valid @RequestBody CategoriaEventoCreateDTO dto) {
        CategoriaEventoResponseDTO response = categoriaEventoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<CategoriaEventoResponseDTO>> listarPorEvento(@PathVariable Long idEvento) {
        return ResponseEntity.ok(categoriaEventoService.listarPorEvento(idEvento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaEventoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaEventoService.obtenerPorId(id));
    }
}
