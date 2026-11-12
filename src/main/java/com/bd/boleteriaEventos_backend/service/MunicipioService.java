package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.MunicipioCreateDTO;
import com.bd.boleteriaEventos_backend.dto.MunicipioResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Municipio;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.MunicipioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MunicipioService {

    private final MunicipioRepository municipioRepository;

    @Transactional
    public MunicipioResponseDTO crear(MunicipioCreateDTO dto) {
        Municipio municipio = Municipio.builder()
                .nombre(dto.nombre().trim())
                .departamento(dto.departamento().trim())
                .build();
        return mapToDTO(municipioRepository.save(municipio));
    }

    @Transactional
    public MunicipioResponseDTO actualizar(Long id, MunicipioCreateDTO dto) {
        Municipio registro = municipioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado con ID: " + id));
        registro.setNombre(dto.nombre().trim());
        registro.setDepartamento(dto.departamento().trim());
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            if (municipioRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Municipio no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
    }

    public List<MunicipioResponseDTO> listarTodos() {
        return municipioRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public MunicipioResponseDTO obtenerPorId(Long id) {
        Municipio m = municipioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado con ID: " + id));
        return mapToDTO(m);
    }

    public MunicipioResponseDTO mapToDTO(Municipio m) {
        return new MunicipioResponseDTO(
                m.getIdMunicipio(),
                m.getNombre(),
                m.getDepartamento()
        );
    }
}
