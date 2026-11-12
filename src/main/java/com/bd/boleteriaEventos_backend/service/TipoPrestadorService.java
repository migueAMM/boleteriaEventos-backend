package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.TipoPrestadorCreateDTO;
import com.bd.boleteriaEventos_backend.dto.TipoPrestadorResponseDTO;
import com.bd.boleteriaEventos_backend.entity.TipoPrestador;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.TipoPrestadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoPrestadorService {

    private final TipoPrestadorRepository tipoPrestadorRepository;

    @Transactional
    public TipoPrestadorResponseDTO crear(TipoPrestadorCreateDTO dto) {
        TipoPrestador tipo = TipoPrestador.builder()
                .nombre(dto.nombre().trim())
                .build();
        return mapToDTO(tipoPrestadorRepository.save(tipo));
    }

    public List<TipoPrestadorResponseDTO> listarTodos() {
        return tipoPrestadorRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TipoPrestadorResponseDTO obtenerPorId(Long id) {
        TipoPrestador t = tipoPrestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de prestador no encontrado con ID: " + id));
        return mapToDTO(t);
    }

    public TipoPrestadorResponseDTO mapToDTO(TipoPrestador t) {
        return new TipoPrestadorResponseDTO(
                t.getIdTipoPrestador(),
                t.getNombre()
        );
    }
}
