package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.ReservaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ReservaResponseDTO;
import com.bd.boleteriaEventos_backend.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(reservaService.listarTodos());
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping("/perfil/{idPerfil}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorPerfil(@PathVariable Long idPerfil) {
        return ResponseEntity.ok(reservaService.listarPorPerfil(idPerfil));
    }
}
