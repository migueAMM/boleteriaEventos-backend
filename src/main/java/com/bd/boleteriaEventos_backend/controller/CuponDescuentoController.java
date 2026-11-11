package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.CuponDescuentoCreateDTO;
import com.bd.boleteriaEventos_backend.dto.CuponDescuentoResponseDTO;
import com.bd.boleteriaEventos_backend.service.CuponDescuentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cupones")
@RequiredArgsConstructor
public class CuponDescuentoController {

    private final CuponDescuentoService cuponDescuentoService;

    @PostMapping
    public ResponseEntity<CuponDescuentoResponseDTO> crear(@Valid @RequestBody CuponDescuentoCreateDTO dto) {
        CuponDescuentoResponseDTO response = cuponDescuentoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<CuponDescuentoResponseDTO>> listarPorEvento(@PathVariable Long idEvento) {
        return ResponseEntity.ok(cuponDescuentoService.listarPorEvento(idEvento));
    }

    @GetMapping("/validar")
    public ResponseEntity<CuponDescuentoResponseDTO> validar(
            @RequestParam Long idEvento,
            @RequestParam String codigo) {
        return ResponseEntity.ok(cuponDescuentoService.validarCupon(idEvento, codigo));
    }
}
