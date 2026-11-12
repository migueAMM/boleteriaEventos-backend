package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.RolCreateDTO;
import com.bd.boleteriaEventos_backend.dto.RolResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Rol;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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

    @Transactional
    public RolResponseDTO actualizar(Long id, RolCreateDTO dto) {
        Rol registro = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        String nombre = dto.nombre().trim().toUpperCase(Locale.ROOT);
        if (rolRepository.findByNombreIgnoreCase(nombre).filter(rol -> !id.equals(rol.getIdRol())).isPresent()) {
            throw new BusinessRuleException("Ya existe un rol con el nombre: " + nombre);
        }
        registro.setNombre(nombre);
        try {
            rolRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("Ya existe un rol con el nombre: " + nombre);
        }
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            if (rolRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Rol no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
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
