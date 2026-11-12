package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.DisponibilidadFuncionDTO;
import com.bd.boleteriaEventos_backend.dto.DisponibilidadZonaDTO;
import com.bd.boleteriaEventos_backend.dto.ZonaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ZonaResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Funcion;
import com.bd.boleteriaEventos_backend.entity.Zona;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.BoletaRepository;
import com.bd.boleteriaEventos_backend.repository.FuncionRepository;
import com.bd.boleteriaEventos_backend.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZonaService {

    private final BoletaRepository boletaRepository;
    private final ZonaRepository zonaRepository;
    private final FuncionRepository funcionRepository;
    private final BloqueoBoleteria bloqueo;

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

    @Transactional
    public ZonaResponseDTO actualizar(Long id, ZonaCreateDTO dto) {
        bloqueo.bloquearEscrituras();
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada con ID: " + id));
        Funcion funcion = funcionRepository.findById(dto.idFuncion())
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + dto.idFuncion()));
        if (!dto.idFuncion().equals(zona.getFuncion().getIdFuncion()) && boletaRepository.existsByZonaIdZona(id)) {
            throw new BusinessRuleException("No se puede cambiar la función de una zona con boletas");
        }
        if (dto.aforo() < boletaRepository.countByZonaIdZonaAndEstadoNot(id, "ANULADA")) {
            throw new BusinessRuleException("El aforo no puede ser inferior al número de boletas que ocupan cupo");
        }
        zona.setFuncion(funcion);
        zona.setNombre(dto.nombre().trim());
        zona.setAforo(dto.aforo());
        zona.setPrecio(dto.precio());
        return mapToDTO(zona);
    }

    @Transactional
    public void eliminar(Long id) {
        bloqueo.bloquearEscrituras();
        try {
            if (zonaRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Zona no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
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

    public DisponibilidadFuncionDTO disponibilidadPorFuncion(Long idFuncion) {
        if (!funcionRepository.existsById(idFuncion)) {
            throw new ResourceNotFoundException("Función no encontrada con ID: " + idFuncion);
        }
        var zonas = zonaRepository.findByFuncionIdFuncion(idFuncion).stream().map(zona -> {
            long ocupadas = boletaRepository.countByZonaIdZonaAndEstadoNot(zona.getIdZona(), "ANULADA");
            return new DisponibilidadZonaDTO(
                    zona.getIdZona(), zona.getNombre(), zona.getAforo(), ocupadas, Math.max(0, zona.getAforo() - ocupadas));
        }).toList();
        return new DisponibilidadFuncionDTO(idFuncion, zonas);
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
