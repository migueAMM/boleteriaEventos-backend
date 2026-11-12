package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.FuncionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.FuncionResponseDTO;
import com.bd.boleteriaEventos_backend.dto.FuncionUpdateDTO;
import com.bd.boleteriaEventos_backend.entity.Atraccion;
import com.bd.boleteriaEventos_backend.entity.Funcion;
import com.bd.boleteriaEventos_backend.entity.Reserva;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.AtraccionRepository;
import com.bd.boleteriaEventos_backend.repository.BoletaRepository;
import com.bd.boleteriaEventos_backend.repository.FuncionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final AtraccionRepository atraccionRepository;
    private final BoletaRepository boletaRepository;
    private final BloqueoBoleteria bloqueo;
    private final NotificacionService notificacionService;

    @Transactional
    public FuncionResponseDTO crear(FuncionCreateDTO dto) {
        bloqueo.bloquearEscrituras();
        if (!dto.horaCierre().isAfter(dto.horaApertura())) {
            throw new BusinessRuleException("La hora de cierre debe ser posterior a la apertura");
        }

        Atraccion atraccion = atraccionRepository.findById(dto.idAtraccion())
                .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrada con ID: " + dto.idAtraccion()));

        validarCruce(dto.idAtraccion(), dto.fecha(), dto.horaApertura(), dto.horaCierre(), null);

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
        boolean yaCancelada = BoletaService.funcionCancelada(funcion);
        funcion.setEstado("CANCELADA");
        var boletas = boletaRepository.findByZonaFuncionIdFuncion(id);
        var reservasAfectadas = new LinkedHashSet<Reserva>();
        for (var boleta : boletas) {
            String estadoAnterior = boleta.getEstado();
            BoletaService.anularVigente(boleta);
            if (!yaCancelada && !Objects.equals(estadoAnterior, boleta.getEstado())) {
                reservasAfectadas.add(boleta.getReserva());
            }
        }
        for (var reserva : reservasAfectadas) {
            notificacionService.notificarReserva(reserva, "Función cancelada",
                    "La función " + id + " fue cancelada. Sus boletas activas para esta función fueron anuladas.");
        }
        return mapToDTO(funcion);
    }

    @Transactional
    public FuncionResponseDTO actualizar(Long id, FuncionUpdateDTO dto) {
        bloqueo.bloquearEscrituras();
        Funcion funcion = funcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + id));
        if (!dto.horaCierre().isAfter(dto.horaApertura())) {
            throw new BusinessRuleException("La hora de cierre debe ser posterior a la apertura");
        }
        BoletaService.validarFuncionEnVenta(funcion, BoletaService.ahora());
        if (boletaRepository.existsByZonaFuncionIdFuncion(id)) {
            throw new BusinessRuleException("No se puede reprogramar una función con boletas");
        }
        Atraccion atraccion = atraccionRepository.findById(dto.idAtraccion())
                .orElseThrow(() -> new ResourceNotFoundException("Atracción no encontrada con ID: " + dto.idAtraccion()));
        validarCruce(dto.idAtraccion(), dto.fecha(), dto.horaApertura(), dto.horaCierre(), id);
        funcion.setAtraccion(atraccion);
        funcion.setFecha(dto.fecha());
        funcion.setHoraApertura(dto.horaApertura());
        funcion.setHoraCierre(dto.horaCierre());
        return mapToDTO(funcion);
    }

    private void validarCruce(Long idAtraccion, LocalDate fecha, LocalTime apertura, LocalTime cierre, Long idExcluir) {
        boolean seCruza = funcionRepository.findByAtraccionIdAtraccion(idAtraccion).stream()
                .filter(funcion -> idExcluir == null || !idExcluir.equals(funcion.getIdFuncion()))
                .filter(funcion -> !BoletaService.funcionCancelada(funcion) && funcion.getFecha().equals(fecha))
                .anyMatch(funcion -> apertura.isBefore(funcion.getHoraCierre()) && cierre.isAfter(funcion.getHoraApertura()));
        if (seCruza) {
            throw new BusinessRuleException("El horario se cruza con otra función de la atracción");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        bloqueo.bloquearEscrituras();
        try {
            if (funcionRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Función no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
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
