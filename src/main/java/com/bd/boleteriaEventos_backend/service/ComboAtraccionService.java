package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.ComboAtraccionResponseDTO;
import com.bd.boleteriaEventos_backend.dto.ComboUpdateDTO.AtraccionReparto;
import com.bd.boleteriaEventos_backend.entity.Combo;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccion;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccionId;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.ComboAtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.ComboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComboAtraccionService {

    private final ComboAtraccionRepository comboAtraccionRepository;
    private final ComboRepository comboRepository;
    private final ComboService comboService;
    private final BloqueoBoleteria bloqueo;

    public List<ComboAtraccionResponseDTO> listarTodos() {
        return comboAtraccionRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public ComboAtraccionResponseDTO obtenerPorId(Long idCombo, Long idAtraccion) {
        return mapToDTO(buscar(idCombo, idAtraccion));
    }

    @Transactional
    public ComboAtraccionResponseDTO crear(Long idCombo, Long idAtraccion, BigDecimal porcentaje) {
        bloqueo.bloquearEscrituras();
        Combo combo = buscarCombo(idCombo);
        if (comboAtraccionRepository.existsById(new ComboAtraccionId(idCombo, idAtraccion))) {
            throw new BusinessRuleException("La atracción ya pertenece al combo");
        }
        List<AtraccionReparto> reparto = repartoSinAtraccion(idCombo, idAtraccion);
        reparto.add(new AtraccionReparto(idAtraccion, porcentaje));
        comboService.actualizarReparto(combo, reparto);
        return mapToDTO(buscar(idCombo, idAtraccion));
    }

    @Transactional
    public ComboAtraccionResponseDTO actualizar(Long idCombo, Long idAtraccion, BigDecimal porcentaje) {
        bloqueo.bloquearEscrituras();
        ComboAtraccion registro = buscar(idCombo, idAtraccion);
        List<AtraccionReparto> reparto = repartoSinAtraccion(idCombo, idAtraccion);
        reparto.add(new AtraccionReparto(idAtraccion, porcentaje));
        comboService.actualizarReparto(registro.getCombo(), reparto);
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long idCombo, Long idAtraccion) {
        bloqueo.bloquearEscrituras();
        ComboAtraccion registro = buscar(idCombo, idAtraccion);
        comboService.actualizarReparto(registro.getCombo(), repartoSinAtraccion(idCombo, idAtraccion));
    }

    private List<AtraccionReparto> repartoSinAtraccion(Long idCombo, Long idAtraccion) {
        List<AtraccionReparto> reparto = new ArrayList<>();
        for (ComboAtraccion registro : comboAtraccionRepository.findByIdIdCombo(idCombo)) {
            Long id = registro.getId().getIdAtraccion();
            if (!id.equals(idAtraccion)) {
                reparto.add(new AtraccionReparto(id, registro.getPorcentajePrecio()));
            }
        }
        return reparto;
    }

    private Combo buscarCombo(Long idCombo) {
        return comboRepository.findById(idCombo)
                .orElseThrow(() -> new ResourceNotFoundException("Combo no encontrado con ID: " + idCombo));
    }

    private ComboAtraccion buscar(Long idCombo, Long idAtraccion) {
        return comboAtraccionRepository.findById(new ComboAtraccionId(idCombo, idAtraccion))
                .orElseThrow(() -> new ResourceNotFoundException("La atracción " + idAtraccion + " no pertenece al combo " + idCombo));
    }

    private ComboAtraccionResponseDTO mapToDTO(ComboAtraccion registro) {
        return new ComboAtraccionResponseDTO(registro.getId().getIdCombo(),
                registro.getId().getIdAtraccion(), registro.getPorcentajePrecio());
    }
}
