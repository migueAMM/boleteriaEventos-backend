package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.ComboCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ComboResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Atraccion;
import com.bd.boleteriaEventos_backend.entity.Combo;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccion;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccionId;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.AtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.ComboAtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.ComboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComboService {

    private final ComboRepository comboRepository;
    private final AtraccionRepository atraccionRepository;
    private final ComboAtraccionRepository comboAtraccionRepository;

    @Transactional
    public ComboResponseDTO crear(ComboCreateDTO dto) {
        List<Long> ids = dto.idsAtracciones();
        if (ids == null || ids.isEmpty() || ids.size() > 10_000 || ids.stream().anyMatch(Objects::isNull)
                || new HashSet<>(ids).size() != ids.size()) {
            throw new BusinessRuleException("Seleccione entre 1 y 10 000 atracciones distintas");
        }

        Combo combo = Combo.builder()
                .nombre(dto.nombre().trim())
                .precio(dto.precio())
                .diasVigencia(dto.diasVigencia())
                .estado(dto.estado().trim().toUpperCase())
                .build();

        Combo guardado = comboRepository.save(combo);

        int centesimasPorAtraccion = 10_000 / ids.size();
        int sobrantes = 10_000 % ids.size();
        for (int i = 0; i < ids.size(); i++) {
            Long idAtraccion = ids.get(i);
            Atraccion atraccion = atraccionRepository.findById(idAtraccion)
                    .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrada con ID: " + idAtraccion));
            int centesimas = centesimasPorAtraccion + (i < sobrantes ? 1 : 0);
            ComboAtraccion comboAtraccion = ComboAtraccion.builder()
                    .id(new ComboAtraccionId(guardado.getIdCombo(), atraccion.getIdAtraccion()))
                    .combo(guardado)
                    .atraccion(atraccion)
                    .porcentajePrecio(BigDecimal.valueOf(centesimas, 2))
                    .build();
            comboAtraccionRepository.save(comboAtraccion);
        }

        return mapToDTO(guardado);
    }

    public List<ComboResponseDTO> listarTodos() {
        return comboRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ComboResponseDTO obtenerPorId(Long id) {
        Combo c = comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo no encontrado con ID: " + id));
        return mapToDTO(c);
    }

    public ComboResponseDTO mapToDTO(Combo c) {
        List<Long> idsAtracciones = comboAtraccionRepository.findByIdIdCombo(c.getIdCombo()).stream()
                .map(ca -> ca.getAtraccion().getIdAtraccion())
                .toList();

        return new ComboResponseDTO(
                c.getIdCombo(),
                c.getNombre(),
                c.getPrecio(),
                c.getDiasVigencia(),
                c.getEstado(),
                idsAtracciones
        );
    }
}
