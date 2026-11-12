package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.PlanCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PlanResponseDTO;
import com.bd.boleteriaEventos_backend.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanResponseDTO> crear(@Valid @RequestBody PlanCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<PlanResponseDTO>> listarTodos() {
        return ResponseEntity.ok(planService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PlanCreateDTO dto) {
        return ResponseEntity.ok(planService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        planService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(planService.obtenerPorId(id));
    }

    @GetMapping("/prestador/{idPrestador}")
    public ResponseEntity<List<PlanResponseDTO>> listarPorPrestador(@PathVariable Long idPrestador) {
        return ResponseEntity.ok(planService.listarPorPrestador(idPrestador));
    }
}
