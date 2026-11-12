package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.AtraccionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.AtraccionResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Atraccion;
import com.bd.boleteriaEventos_backend.entity.Prestador;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.AtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.PrestadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AtraccionService {

    private final AtraccionRepository atraccionRepository;
    private final PrestadorRepository prestadorRepository;

    @Transactional
    public AtraccionResponseDTO crear(AtraccionCreateDTO dto) {
        Prestador prestador = prestadorRepository.findById(dto.idPrestador())
                .orElseThrow(() -> new ResourceNotFoundException("Prestador no encontrado con ID: " + dto.idPrestador()));

        Atraccion atraccion = Atraccion.builder()
                .nombre(dto.nombre().trim())
                .descripcion(dto.descripcion())
                .prestador(prestador)
                .build();

        return mapToDTO(atraccionRepository.save(atraccion));
    }

    @Transactional
    public AtraccionResponseDTO actualizar(Long id, AtraccionCreateDTO dto) {
        Atraccion registro = atraccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrado con ID: " + id));
        registro.setPrestador(prestadorRepository.findById(dto.idPrestador())
                .orElseThrow(() -> new ResourceNotFoundException("Prestador no encontrado con ID: " + dto.idPrestador())));
        registro.setNombre(dto.nombre().trim());
        registro.setDescripcion(dto.descripcion());
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            if (atraccionRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Atracción no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
    }

    public List<AtraccionResponseDTO> listarTodos() {
        return atraccionRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public AtraccionResponseDTO obtenerPorId(Long id) {
        Atraccion a = atraccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrada con ID: " + id));
        return mapToDTO(a);
    }

    public List<AtraccionResponseDTO> listarPorPrestador(Long idPrestador) {
        return atraccionRepository.findByPrestadorIdPrestador(idPrestador).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public AtraccionResponseDTO mapToDTO(Atraccion a) {
        return new AtraccionResponseDTO(
                a.getIdAtraccion(),
                a.getNombre(),
                a.getDescripcion(),
                a.getPrestador() != null ? a.getPrestador().getIdPrestador() : null,
                a.getPrestador() != null ? a.getPrestador().getNombre() : null
        );
    }
}
