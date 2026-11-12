package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.PerfilClienteCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PerfilClienteResponseDTO;
import com.bd.boleteriaEventos_backend.entity.PerfilCliente;
import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.PerfilClienteRepository;
import com.bd.boleteriaEventos_backend.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerfilClienteService {

    private final PerfilClienteRepository perfilClienteRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;

    @Transactional
    public PerfilClienteResponseDTO crear(PerfilClienteCreateDTO dto) {
        if (perfilClienteRepository.findByDocumento(dto.documento().trim()).isPresent()) {
            throw new BusinessRuleException("Ya existe un perfil con el documento: " + dto.documento());
        }

        if (perfilClienteRepository.findByUsuarioIdUsuario(dto.idUsuario()).isPresent()) {
            throw new BusinessRuleException("El usuario ya tiene un perfil de cliente registrado");
        }

        UsuarioSistema usuario = usuarioSistemaRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.idUsuario()));

        PerfilCliente perfil = PerfilCliente.builder()
                .documento(dto.documento().trim())
                .nombre(dto.nombre().trim())
                .telefono(dto.telefono() != null ? dto.telefono().trim() : null)
                .usuario(usuario)
                .build();

        return mapToDTO(perfilClienteRepository.save(perfil));
    }

    public List<PerfilClienteResponseDTO> listarTodos() {
        return perfilClienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PerfilClienteResponseDTO obtenerPorId(Long id) {
        PerfilCliente p = perfilClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado con ID: " + id));
        return mapToDTO(p);
    }

    public PerfilClienteResponseDTO obtenerPorUsuario(Long idUsuario) {
        PerfilCliente p = perfilClienteRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para el usuario: " + idUsuario));
        return mapToDTO(p);
    }

    public PerfilClienteResponseDTO mapToDTO(PerfilCliente p) {
        return new PerfilClienteResponseDTO(
                p.getIdPerfil(),
                p.getDocumento(),
                p.getNombre(),
                p.getTelefono(),
                p.getUsuario() != null ? p.getUsuario().getIdUsuario() : null,
                p.getUsuario() != null ? p.getUsuario().getCorreo() : null
        );
    }
}
