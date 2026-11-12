package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.MunicipioCreateDTO;
import com.bd.boleteriaEventos_backend.dto.MunicipioResponseDTO;
import com.bd.boleteriaEventos_backend.service.MunicipioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/municipios")
@RequiredArgsConstructor
public class MunicipioController {

    private final MunicipioService municipioService;

    @PostMapping
    public ResponseEntity<MunicipioResponseDTO> crear(@Valid @RequestBody MunicipioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(municipioService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<MunicipioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(municipioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MunicipioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(municipioService.obtenerPorId(id));
    }
}
