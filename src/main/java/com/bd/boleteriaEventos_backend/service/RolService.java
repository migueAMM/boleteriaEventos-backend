package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.RolCreateDTO;
import com.bd.boleteriaEventos_backend.dto.RolResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Rol;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolService {

    private final RolRepository rolRepository;

    @Transactional
    public RolResponseDTO crear(RolCreateDTO dto) {
        if (rolRepository.findByNombreIgnoreCase(dto.nombre().trim()).isPresent()) {
            throw new BusinessRuleException("Ya existe un rol con el nombre: " + dto.nombre());
        }

        Rol rol = Rol.builder()
                .nombre(dto.nombre().trim().toUpperCase())
                .build();

        return mapToDTO(rolRepository.save(rol));
    }

    public List<RolResponseDTO> listarTodos() {
        return rolRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public RolResponseDTO obtenerPorId(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        return mapToDTO(rol);
    }

    public RolResponseDTO mapToDTO(Rol r) {
        return new RolResponseDTO(
                r.getIdRol(),
                r.getNombre()
        );
    }
}
