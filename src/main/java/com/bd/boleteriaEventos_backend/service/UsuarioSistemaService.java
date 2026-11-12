package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Rol;
import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.RolRepository;
import com.bd.boleteriaEventos_backend.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioSistemaService {

    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final RolRepository rolRepository;

    @Transactional
    public UsuarioSistemaResponseDTO crear(UsuarioSistemaCreateDTO dto) {
        if (usuarioSistemaRepository.findByCorreoIgnoreCase(dto.correo().trim()).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario con el correo: " + dto.correo());
        }

        Rol rol = rolRepository.findById(dto.idRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + dto.idRol()));

        UsuarioSistema usuario = UsuarioSistema.builder()
                .correo(dto.correo().trim().toLowerCase())
                .claveHash(dto.clave()) // En entorno de producción se aplica PasswordEncoder (BCrypt)
                .estado(dto.estado().trim().toUpperCase())
                .rol(rol)
                .build();

        return mapToDTO(usuarioSistemaRepository.save(usuario));
    }

    public List<UsuarioSistemaResponseDTO> listarTodos() {
        return usuarioSistemaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public UsuarioSistemaResponseDTO obtenerPorId(Long id) {
        UsuarioSistema u = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario del sistema no encontrado con ID: " + id));
        return mapToDTO(u);
    }

    public UsuarioSistemaResponseDTO mapToDTO(UsuarioSistema u) {
        return new UsuarioSistemaResponseDTO(
                u.getIdUsuario(),
                u.getCorreo(),
                u.getEstado(),
                u.getRol() != null ? u.getRol().getIdRol() : null,
                u.getRol() != null ? u.getRol().getNombre() : null
        );
    }
}
