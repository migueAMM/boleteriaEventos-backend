package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.OrganizadorCreateDTO;
import com.bd.boleteriaEventos_backend.dto.OrganizadorResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Organizador;
import com.bd.boleteriaEventos_backend.entity.Usuario;
import com.bd.boleteriaEventos_backend.enums.Rol;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.OrganizadorRepository;
import com.bd.boleteriaEventos_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizadorService {

    private final OrganizadorRepository organizadorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public OrganizadorResponseDTO crear(OrganizadorCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.idUsuario()));

        if (usuario.getRol() != Rol.ORGANIZADOR && usuario.getRol() != Rol.ADMIN) {
            throw new BusinessRuleException("El usuario debe tener el rol ORGANIZADOR o ADMIN para registrar una entidad organizadora");
        }

        if (organizadorRepository.findByNit(dto.nit()).isPresent()) {
            throw new BusinessRuleException("Ya existe un organizador registrado con el NIT: " + dto.nit());
        }

        if (organizadorRepository.findByUsuarioIdUsuario(dto.idUsuario()).isPresent()) {
            throw new BusinessRuleException("El usuario ya tiene una entidad organizadora registrada");
        }

        Organizador organizador = Organizador.builder()
                .usuario(usuario)
                .nit(dto.nit())
                .razonSocial(dto.razonSocial())
                .nombreComercial(dto.nombreComercial())
                .descripcion(dto.descripcion())
                .sitioWeb(dto.sitioWeb())
                .telefonoContacto(dto.telefonoContacto())
                .verificado(false)
                .build();

        return mapToDTO(organizadorRepository.save(organizador));
    }

    public OrganizadorResponseDTO obtenerPorId(Long id) {
        Organizador organizador = organizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador no encontrado con ID: " + id));
        return mapToDTO(organizador);
    }

    public List<OrganizadorResponseDTO> listarTodos() {
        return organizadorRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public OrganizadorResponseDTO mapToDTO(Organizador o) {
        String nombreUsuario = o.getUsuario() != null
                ? o.getUsuario().getNombres() + " " + o.getUsuario().getApellidos()
                : null;
        Long idUsuario = o.getUsuario() != null ? o.getUsuario().getIdUsuario() : null;

        return new OrganizadorResponseDTO(
                o.getIdOrganizador(),
                idUsuario,
                nombreUsuario,
                o.getNit(),
                o.getRazonSocial(),
                o.getNombreComercial(),
                o.getDescripcion(),
                o.getSitioWeb(),
                o.getTelefonoContacto(),
                o.getVerificado()
        );
    }
}
