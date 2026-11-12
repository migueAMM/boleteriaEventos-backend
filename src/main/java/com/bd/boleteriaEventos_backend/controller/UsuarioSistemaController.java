package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.LoginRequestDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaResponseDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaUpdateDTO;
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

    @PostMapping("/login")
    public ResponseEntity<UsuarioSistemaResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return usuarioSistemaService.autenticar(dto).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping
    public ResponseEntity<List<UsuarioSistemaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioSistemaService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioSistemaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioSistemaUpdateDTO dto) {
        return ResponseEntity.ok(usuarioSistemaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioSistemaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSistemaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioSistemaService.obtenerPorId(id));
    }
}
