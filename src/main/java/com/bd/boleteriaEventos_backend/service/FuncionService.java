package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.FuncionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.FuncionResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Atraccion;
import com.bd.boleteriaEventos_backend.entity.Funcion;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.AtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.BoletaRepository;
import com.bd.boleteriaEventos_backend.repository.FuncionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final AtraccionRepository atraccionRepository;
    private final BoletaRepository boletaRepository;
    private final BloqueoBoleteria bloqueo;

    @Transactional
    public FuncionResponseDTO crear(FuncionCreateDTO dto) {
        bloqueo.bloquearEscrituras();
        if (!dto.horaCierre().isAfter(dto.horaApertura())) {
            throw new BusinessRuleException("La hora de cierre debe ser posterior a la apertura");
        }

        Atraccion atraccion = atraccionRepository.findById(dto.idAtraccion())
                .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrada con ID: " + dto.idAtraccion()));

        boolean seCruza = funcionRepository.findByAtraccionIdAtraccion(dto.idAtraccion()).stream()
                .filter(funcion -> !BoletaService.funcionCancelada(funcion) && funcion.getFecha().equals(dto.fecha()))
                .anyMatch(funcion -> dto.horaApertura().isBefore(funcion.getHoraCierre())
                        && dto.horaCierre().isAfter(funcion.getHoraApertura()));
        if (seCruza) {
            throw new BusinessRuleException("El horario se cruza con otra función de la atracción");
        }

        Funcion funcion = Funcion.builder()
                .fecha(dto.fecha())
                .horaApertura(dto.horaApertura())
                .horaCierre(dto.horaCierre())
                .estado(dto.estado().trim().toUpperCase())
                .atraccion(atraccion)
                .build();

        return mapToDTO(funcionRepository.save(funcion));
    }

    @Transactional
    public FuncionResponseDTO cancelar(Long id) {
        bloqueo.bloquearEscrituras();
        Funcion funcion = funcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + id));
        funcion.setEstado("CANCELADA");
        boletaRepository.findByZonaFuncionIdFuncion(id).forEach(BoletaService::anularVigente);
        return mapToDTO(funcion);
    }

    public List<FuncionResponseDTO> listarTodos() {
        return funcionRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public FuncionResponseDTO obtenerPorId(Long id) {
        Funcion f = funcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + id));
        return mapToDTO(f);
    }

    public List<FuncionResponseDTO> listarPorAtraccion(Long idAtraccion) {
        return funcionRepository.findByAtraccionIdAtraccion(idAtraccion).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public FuncionResponseDTO mapToDTO(Funcion f) {
        return new FuncionResponseDTO(
                f.getIdFuncion(),
                f.getFecha(),
                f.getHoraApertura(),
                f.getHoraCierre(),
                f.getEstado(),
                f.getAtraccion() != null ? f.getAtraccion().getIdAtraccion() : null,
                f.getAtraccion() != null ? f.getAtraccion().getNombre() : null
        );
    }
}
