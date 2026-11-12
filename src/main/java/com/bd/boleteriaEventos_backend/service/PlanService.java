package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.PlanCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PlanResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Plan;
import com.bd.boleteriaEventos_backend.entity.Prestador;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.PlanRepository;
import com.bd.boleteriaEventos_backend.repository.PrestadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;
    private final PrestadorRepository prestadorRepository;

    @Transactional
    public PlanResponseDTO crear(PlanCreateDTO dto) {
        Prestador prestador = prestadorRepository.findById(dto.idPrestador())
                .orElseThrow(() -> new ResourceNotFoundException("Prestador no encontrado con ID: " + dto.idPrestador()));

        Plan plan = Plan.builder()
                .nombre(dto.nombre().trim())
                .descripcion(dto.descripcion())
                .estado(dto.estado().trim().toUpperCase())
                .prestador(prestador)
                .build();

        return mapToDTO(planRepository.save(plan));
    }

    @Transactional
    public PlanResponseDTO actualizar(Long id, PlanCreateDTO dto) {
        Plan registro = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));
        registro.setPrestador(prestadorRepository.findById(dto.idPrestador())
                .orElseThrow(() -> new ResourceNotFoundException("Prestador no encontrado con ID: " + dto.idPrestador())));
        registro.setNombre(dto.nombre().trim());
        registro.setDescripcion(dto.descripcion());
        registro.setEstado(dto.estado().trim().toUpperCase(Locale.ROOT));
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            if (planRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Plan no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
    }

    public List<PlanResponseDTO> listarTodos() {
        return planRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PlanResponseDTO obtenerPorId(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));
        return mapToDTO(plan);
    }

    public List<PlanResponseDTO> listarPorPrestador(Long idPrestador) {
        return planRepository.findByPrestadorIdPrestador(idPrestador).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PlanResponseDTO mapToDTO(Plan p) {
        return new PlanResponseDTO(
                p.getIdPlan(),
                p.getNombre(),
                p.getDescripcion(),
                p.getEstado(),
                p.getPrestador() != null ? p.getPrestador().getIdPrestador() : null,
                p.getPrestador() != null ? p.getPrestador().getNombre() : null
        );
    }
}
