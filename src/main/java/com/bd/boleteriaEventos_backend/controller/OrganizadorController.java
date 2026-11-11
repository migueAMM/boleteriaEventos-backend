package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.OrganizadorCreateDTO;
import com.bd.boleteriaEventos_backend.dto.OrganizadorResponseDTO;
import com.bd.boleteriaEventos_backend.service.OrganizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizadores")
@RequiredArgsConstructor
public class OrganizadorController {

    private final OrganizadorService organizadorService;

    @PostMapping
    public ResponseEntity<OrganizadorResponseDTO> crear(@Valid @RequestBody OrganizadorCreateDTO dto) {
        OrganizadorResponseDTO response = organizadorService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrganizadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(organizadorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizadorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(organizadorService.obtenerPorId(id));
    }
}
