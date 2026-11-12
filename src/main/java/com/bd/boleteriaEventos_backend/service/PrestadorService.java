package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.PrestadorCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PrestadorResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Municipio;
import com.bd.boleteriaEventos_backend.entity.Prestador;
import com.bd.boleteriaEventos_backend.entity.TipoPrestador;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.MunicipioRepository;
import com.bd.boleteriaEventos_backend.repository.PrestadorRepository;
import com.bd.boleteriaEventos_backend.repository.TipoPrestadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrestadorService {

    private final PrestadorRepository prestadorRepository;
    private final MunicipioRepository municipioRepository;
    private final TipoPrestadorRepository tipoPrestadorRepository;

    @Transactional
    public PrestadorResponseDTO crear(PrestadorCreateDTO dto) {
        if (prestadorRepository.findByNit(dto.nit()).isPresent()) {
            throw new BusinessRuleException("Ya existe un prestador registrado con el NIT: " + dto.nit());
        }

        Municipio municipio = municipioRepository.findById(dto.idMunicipio())
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado con ID: " + dto.idMunicipio()));

        TipoPrestador tipoPrestador = tipoPrestadorRepository.findById(dto.idTipoPrestador())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de prestador no encontrado con ID: " + dto.idTipoPrestador()));

        Prestador prestador = Prestador.builder()
                .nit(dto.nit().trim())
                .nombre(dto.nombre().trim())
                .correo(dto.correo().trim().toLowerCase())
                .municipio(municipio)
                .tipoPrestador(tipoPrestador)
                .build();

        return mapToDTO(prestadorRepository.save(prestador));
    }

    public List<PrestadorResponseDTO> listarTodos() {
        return prestadorRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PrestadorResponseDTO obtenerPorId(Long id) {
        Prestador p = prestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestador no encontrado con ID: " + id));
        return mapToDTO(p);
    }

    public PrestadorResponseDTO mapToDTO(Prestador p) {
        return new PrestadorResponseDTO(
                p.getIdPrestador(),
                p.getNit(),
                p.getNombre(),
                p.getCorreo(),
                p.getMunicipio() != null ? p.getMunicipio().getIdMunicipio() : null,
                p.getMunicipio() != null ? p.getMunicipio().getNombre() : null,
                p.getTipoPrestador() != null ? p.getTipoPrestador().getIdTipoPrestador() : null,
                p.getTipoPrestador() != null ? p.getTipoPrestador().getNombre() : null
        );
    }
}
