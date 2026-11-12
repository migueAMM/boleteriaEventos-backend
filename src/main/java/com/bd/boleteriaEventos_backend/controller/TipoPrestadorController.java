package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.TipoPrestadorCreateDTO;
import com.bd.boleteriaEventos_backend.dto.TipoPrestadorResponseDTO;
import com.bd.boleteriaEventos_backend.service.TipoPrestadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-prestador")
@RequiredArgsConstructor
public class TipoPrestadorController {

    private final TipoPrestadorService tipoPrestadorService;

    @PostMapping
    public ResponseEntity<TipoPrestadorResponseDTO> crear(@Valid @RequestBody TipoPrestadorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoPrestadorService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<TipoPrestadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(tipoPrestadorService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoPrestadorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TipoPrestadorCreateDTO dto) {
        return ResponseEntity.ok(tipoPrestadorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoPrestadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPrestadorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoPrestadorService.obtenerPorId(id));
    }
}
