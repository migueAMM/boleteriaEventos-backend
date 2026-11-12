package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.PrestadorCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PrestadorResponseDTO;
import com.bd.boleteriaEventos_backend.service.PrestadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestadores")
@RequiredArgsConstructor
public class PrestadorController {

    private final PrestadorService prestadorService;

    @PostMapping
    public ResponseEntity<PrestadorResponseDTO> crear(@Valid @RequestBody PrestadorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestadorService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrestadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(prestadorService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestadorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PrestadorCreateDTO dto) {
        return ResponseEntity.ok(prestadorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prestadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestadorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestadorService.obtenerPorId(id));
    }
}
