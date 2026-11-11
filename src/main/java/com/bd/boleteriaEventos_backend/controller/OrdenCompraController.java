package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.OrdenCompraCreateDTO;
import com.bd.boleteriaEventos_backend.dto.OrdenCompraResponseDTO;
import com.bd.boleteriaEventos_backend.service.OrdenCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @PostMapping
    public ResponseEntity<OrdenCompraResponseDTO> crear(@Valid @RequestBody OrdenCompraCreateDTO dto) {
        OrdenCompraResponseDTO response = ordenCompraService.crearOrden(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<OrdenCompraResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ordenCompraService.obtenerPorCodigo(codigo));
    }

    @GetMapping("/comprador/{idComprador}")
    public ResponseEntity<List<OrdenCompraResponseDTO>> listarPorComprador(@PathVariable Long idComprador) {
        return ResponseEntity.ok(ordenCompraService.listarPorComprador(idComprador));
    }
}
