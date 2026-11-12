package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.ComboCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ComboResponseDTO;
import com.bd.boleteriaEventos_backend.dto.ComboUpdateDTO;
import com.bd.boleteriaEventos_backend.entity.Atraccion;
import com.bd.boleteriaEventos_backend.entity.Combo;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccion;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccionId;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.AtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.BoletaRepository;
import com.bd.boleteriaEventos_backend.repository.ComboAtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.ComboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComboService {

    private final ComboRepository comboRepository;
    private final AtraccionRepository atraccionRepository;
    private final ComboAtraccionRepository comboAtraccionRepository;
    private final BoletaRepository boletaRepository;
    private final BloqueoBoleteria bloqueo;

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

    @Transactional
    public ComboResponseDTO actualizar(Long id, ComboUpdateDTO dto) {
        bloqueo.bloquearEscrituras();
        Combo combo = buscar(id);
        if (dto.atracciones() != null) {
            actualizarReparto(combo, dto.atracciones());
        }
        combo.setNombre(dto.nombre().trim());
        combo.setPrecio(dto.precio());
        combo.setDiasVigencia(dto.diasVigencia());
        combo.setEstado(dto.estado().trim().toUpperCase(Locale.ROOT));
        return mapToDTO(combo);
    }

    void actualizarReparto(Combo combo, List<ComboUpdateDTO.AtraccionReparto> reparto) {
        BigDecimal suma = reparto.stream().map(ComboUpdateDTO.AtraccionReparto::porcentajePrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var solicitadas = new LinkedHashMap<Long, BigDecimal>();
        for (var item : reparto) {
            if (solicitadas.putIfAbsent(item.idAtraccion(), item.porcentajePrecio().setScale(2)) != null) {
                throw new BusinessRuleException("Una atracción no puede repetirse en el combo");
            }
        }
        if (suma.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new BusinessRuleException("Los porcentajes de reparto deben sumar 100 %");
        }

        Long id = combo.getIdCombo();
        List<ComboAtraccion> actuales = comboAtraccionRepository.findByIdIdCombo(id);
        boolean mismoReparto = actuales.size() == solicitadas.size() && actuales.stream().allMatch(actual -> {
            BigDecimal porcentaje = solicitadas.get(actual.getId().getIdAtraccion());
            return porcentaje != null && porcentaje.compareTo(actual.getPorcentajePrecio()) == 0;
        });
        if (mismoReparto) {
            return;
        }
        validarSinCompras(id);

        for (ComboAtraccion actual : actuales) {
            BigDecimal porcentaje = solicitadas.remove(actual.getId().getIdAtraccion());
            if (porcentaje == null) {
                comboAtraccionRepository.delete(actual);
            } else {
                actual.setPorcentajePrecio(porcentaje);
            }
        }
        solicitadas.forEach((idAtraccion, porcentaje) -> {
            Atraccion atraccion = atraccionRepository.findById(idAtraccion)
                    .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrada con ID: " + idAtraccion));
            comboAtraccionRepository.save(ComboAtraccion.builder()
                    .id(new ComboAtraccionId(id, idAtraccion)).combo(combo).atraccion(atraccion)
                    .porcentajePrecio(porcentaje).build());
        });
    }

    @Transactional
    public void eliminar(Long id) {
        bloqueo.bloquearEscrituras();
        buscar(id);
        validarSinCompras(id);
        if (!comboAtraccionRepository.findByIdIdCombo(id).isEmpty()) {
            throw new BusinessRuleException("No se puede eliminar un combo con atracciones asociadas");
        }
        try {
            comboRepository.eliminarSinCascada(id);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El combo tiene dependencias y no se puede eliminar");
        }
    }

    private void validarSinCompras(Long id) {
        if (boletaRepository.existsByComboIdCombo(id)) {
            throw new BusinessRuleException("El combo tiene boletas asociadas y su composición debe conservarse");
        }
    }

    private Combo buscar(Long id) {
        return comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo no encontrado con ID: " + id));
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
