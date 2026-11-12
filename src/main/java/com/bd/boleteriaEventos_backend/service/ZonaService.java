package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.ZonaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ZonaResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Funcion;
import com.bd.boleteriaEventos_backend.entity.Zona;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.FuncionRepository;
import com.bd.boleteriaEventos_backend.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZonaService {

    private final ZonaRepository zonaRepository;
    private final FuncionRepository funcionRepository;

    @Transactional
    public ZonaResponseDTO crear(ZonaCreateDTO dto) {
        Funcion funcion = funcionRepository.findById(dto.idFuncion())
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + dto.idFuncion()));

        Zona zona = Zona.builder()
                .nombre(dto.nombre().trim())
                .aforo(dto.aforo())
                .precio(dto.precio())
                .funcion(funcion)
                .build();

        return mapToDTO(zonaRepository.save(zona));
    }

    public List<ZonaResponseDTO> listarTodos() {
        return zonaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ZonaResponseDTO obtenerPorId(Long id) {
        Zona z = zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada con ID: " + id));
        return mapToDTO(z);
    }

    public List<ZonaResponseDTO> listarPorFuncion(Long idFuncion) {
        return zonaRepository.findByFuncionIdFuncion(idFuncion).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ZonaResponseDTO mapToDTO(Zona z) {
        return new ZonaResponseDTO(
                z.getIdZona(),
                z.getNombre(),
                z.getAforo(),
                z.getPrecio(),
                z.getFuncion() != null ? z.getFuncion().getIdFuncion() : null
        );
    }
}
