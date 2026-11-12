package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.TipoEntradaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.TipoEntradaResponseDTO;
import com.bd.boleteriaEventos_backend.service.TipoEntradaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-entrada")
@RequiredArgsConstructor
public class TipoEntradaController {

    private final TipoEntradaService tipoEntradaService;

    @PostMapping
    public ResponseEntity<TipoEntradaResponseDTO> crear(@Valid @RequestBody TipoEntradaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoEntradaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<TipoEntradaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(tipoEntradaService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoEntradaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TipoEntradaCreateDTO dto) {
        return ResponseEntity.ok(tipoEntradaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoEntradaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoEntradaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoEntradaService.obtenerPorId(id));
    }
}
