package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.UsuarioCreateDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Usuario;
import com.bd.boleteriaEventos_backend.enums.Rol;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponseDTO crear(UsuarioCreateDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el email: " + dto.email());
        }
        if (usuarioRepository.findByNumeroDocumento(dto.numeroDocumento()).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el documento: " + dto.numeroDocumento());
        }

        Usuario usuario = Usuario.builder()
                .tipoDocumento(dto.tipoDocumento())
                .numeroDocumento(dto.numeroDocumento())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .passwordHash(hashPassword(dto.password()))
                .telefono(dto.telefono())
                .fechaNacimiento(dto.fechaNacimiento())
                .genero(dto.genero())
                .rol(dto.rol() != null ? dto.rol() : Rol.COMPRADOR)
                .fechaRegistro(LocalDateTime.now())
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToDTO(guardado);
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToDTO(usuario);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        usuarioRepository.findByEmail(dto.email()).ifPresent(existente -> {
            if (!existente.getIdUsuario().equals(id)) {
                throw new BusinessRuleException("El email ya está en uso por otro usuario: " + dto.email());
            }
        });

        usuarioRepository.findByNumeroDocumento(dto.numeroDocumento()).ifPresent(existente -> {
            if (!existente.getIdUsuario().equals(id)) {
                throw new BusinessRuleException("El documento ya está en uso por otro usuario: " + dto.numeroDocumento());
            }
        });

        usuario.setTipoDocumento(dto.tipoDocumento());
        usuario.setNumeroDocumento(dto.numeroDocumento());
        usuario.setNombres(dto.nombres());
        usuario.setApellidos(dto.apellidos());
        usuario.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            usuario.setPasswordHash(hashPassword(dto.password()));
        }
        usuario.setTelefono(dto.telefono());
        usuario.setFechaNacimiento(dto.fechaNacimiento());
        usuario.setGenero(dto.genero());
        if (dto.rol() != null) {
            usuario.setRol(dto.rol());
        }

        return mapToDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public UsuarioResponseDTO mapToDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getIdUsuario(),
                u.getTipoDocumento(),
                u.getNumeroDocumento(),
                u.getNombres(),
                u.getApellidos(),
                u.getEmail(),
                u.getTelefono(),
                u.getFechaNacimiento(),
                u.getGenero(),
                u.getRol(),
                u.getFechaRegistro(),
                u.getActivo()
        );
    }
}
