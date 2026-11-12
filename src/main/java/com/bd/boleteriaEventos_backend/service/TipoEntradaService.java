package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.TipoEntradaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.TipoEntradaResponseDTO;
import com.bd.boleteriaEventos_backend.entity.TipoEntrada;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.TipoEntradaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoEntradaService {

    private final TipoEntradaRepository tipoEntradaRepository;

    @Transactional
    public TipoEntradaResponseDTO crear(TipoEntradaCreateDTO dto) {
        if (Boolean.TRUE.equals(dto.esAdulto()) && Boolean.TRUE.equals(dto.requiereAdulto())) {
            throw new BusinessRuleException("Una entrada de adulto no puede requerir acompañamiento");
        }
        TipoEntrada tipo = TipoEntrada.builder()
                .nombre(dto.nombre().trim())
                .porcentajeDescuento(dto.porcentajeDescuento())
                .requiereAdulto(dto.requiereAdulto())
                .esAdulto(dto.esAdulto())
                .build();

        return mapToDTO(tipoEntradaRepository.save(tipo));
    }

    public List<TipoEntradaResponseDTO> listarTodos() {
        return tipoEntradaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TipoEntradaResponseDTO obtenerPorId(Long id) {
        TipoEntrada t = tipoEntradaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de entrada no encontrado con ID: " + id));
        return mapToDTO(t);
    }

    public TipoEntradaResponseDTO mapToDTO(TipoEntrada t) {
        return new TipoEntradaResponseDTO(
                t.getIdTipoEntrada(),
                t.getNombre(),
                t.getPorcentajeDescuento(),
                t.getRequiereAdulto(),
                t.admiteAcompanamiento()
        );
    }
}
