package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.CuponDescuentoCreateDTO;
import com.bd.boleteriaEventos_backend.dto.CuponDescuentoResponseDTO;
import com.bd.boleteriaEventos_backend.entity.CuponDescuento;
import com.bd.boleteriaEventos_backend.entity.Evento;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.CuponDescuentoRepository;
import com.bd.boleteriaEventos_backend.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuponDescuentoService {

    private final CuponDescuentoRepository cuponDescuentoRepository;
    private final EventoRepository eventoRepository;

    @Transactional
    public CuponDescuentoResponseDTO crear(CuponDescuentoCreateDTO dto) {
        Evento evento = eventoRepository.findById(dto.idEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + dto.idEvento()));

        String codigoUpper = dto.codigo().trim().toUpperCase();

        if (cuponDescuentoRepository.findByEventoIdEventoAndCodigo(dto.idEvento(), codigoUpper).isPresent()) {
            throw new BusinessRuleException("Ya existe un cupón con el código '" + codigoUpper + "' para este evento");
        }

        CuponDescuento cupon = CuponDescuento.builder()
                .evento(evento)
                .codigo(codigoUpper)
                .porcentajeDescuento(dto.porcentajeDescuento())
                .usosMaximos(dto.usosMaximos())
                .usosActuales(0)
                .fechaExpiracion(dto.fechaExpiracion())
                .activo(true)
                .build();

        return mapToDTO(cuponDescuentoRepository.save(cupon));
    }

    public CuponDescuentoResponseDTO validarCupon(Long idEvento, String codigo) {
        String codigoUpper = codigo.trim().toUpperCase();
        CuponDescuento cupon = cuponDescuentoRepository.findByEventoIdEventoAndCodigo(idEvento, codigoUpper)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón '" + codigoUpper + "' no encontrado para este evento"));

        if (!Boolean.TRUE.equals(cupon.getActivo())) {
            throw new BusinessRuleException("El cupón '" + codigoUpper + "' está inactivo");
        }

        if (cupon.getFechaExpiracion() != null && cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("El cupón '" + codigoUpper + "' ha expirado");
        }

        if (cupon.getUsosMaximos() != null && cupon.getUsosActuales() >= cupon.getUsosMaximos()) {
            throw new BusinessRuleException("El cupón '" + codigoUpper + "' ha alcanzado el límite máximo de usos");
        }

        return mapToDTO(cupon);
    }

    public List<CuponDescuentoResponseDTO> listarPorEvento(Long idEvento) {
        return cuponDescuentoRepository.findByEventoIdEvento(idEvento).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CuponDescuentoResponseDTO mapToDTO(CuponDescuento c) {
        return new CuponDescuentoResponseDTO(
                c.getIdCupon(),
                c.getEvento() != null ? c.getEvento().getIdEvento() : null,
                c.getCodigo(),
                c.getPorcentajeDescuento(),
                c.getUsosMaximos(),
                c.getUsosActuales(),
                c.getFechaExpiracion(),
                c.getActivo()
        );
    }
}
