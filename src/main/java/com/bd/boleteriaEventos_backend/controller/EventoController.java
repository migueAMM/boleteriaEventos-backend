package com.bd.boleteriaEventos_backend.controller;

import com.bd.boleteriaEventos_backend.dto.EventoCreateDTO;
import com.bd.boleteriaEventos_backend.dto.EventoResponseDTO;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import com.bd.boleteriaEventos_backend.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoCreateDTO dto) {
        EventoResponseDTO response = eventoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(eventoService.filtrar(ciudad, tipo, fechaInicio, fechaFin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<EventoResponseDTO> obtenerPorSlug(@PathVariable String slug) {
        return ResponseEntity.ok(eventoService.obtenerPorSlug(slug));
    }

    @PatchMapping("/{id}/publicar")
    public ResponseEntity<EventoResponseDTO> publicar(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.publicar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.cancelar(id));
    }
}
