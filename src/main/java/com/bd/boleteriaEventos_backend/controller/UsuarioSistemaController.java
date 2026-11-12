package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaResponseDTO;
import com.bd.boleteriaEventos_backend.service.UsuarioSistemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios-sistema")
@RequiredArgsConstructor
public class UsuarioSistemaController {

    private final UsuarioSistemaService usuarioSistemaService;

    @PostMapping
    public ResponseEntity<UsuarioSistemaResponseDTO> crear(@Valid @RequestBody UsuarioSistemaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSistemaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioSistemaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioSistemaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSistemaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioSistemaService.obtenerPorId(id));
    }
}
