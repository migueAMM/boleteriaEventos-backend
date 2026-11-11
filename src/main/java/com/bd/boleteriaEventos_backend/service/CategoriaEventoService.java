package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.CategoriaEventoCreateDTO;
import com.bd.boleteriaEventos_backend.dto.CategoriaEventoResponseDTO;
import com.bd.boleteriaEventos_backend.entity.CategoriaEvento;
import com.bd.boleteriaEventos_backend.entity.Evento;
import com.bd.boleteriaEventos_backend.enums.EstadoCategoria;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.CategoriaEventoRepository;
import com.bd.boleteriaEventos_backend.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaEventoService {

    private final CategoriaEventoRepository categoriaEventoRepository;
    private final EventoRepository eventoRepository;

    @Transactional
    public CategoriaEventoResponseDTO crear(CategoriaEventoCreateDTO dto) {
        Evento evento = eventoRepository.findById(dto.idEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + dto.idEvento()));

        if (dto.edadMinima() != null && dto.edadMaxima() != null && dto.edadMinima() > dto.edadMaxima()) {
            throw new BusinessRuleException("La edad mínima no puede ser mayor que la edad máxima");
        }

        if (dto.fechaInicioVenta() != null && dto.fechaFinVenta() != null && dto.fechaInicioVenta().isAfter(dto.fechaFinVenta())) {
            throw new BusinessRuleException("La fecha de inicio de venta no puede ser posterior a la fecha de fin de venta");
        }

        CategoriaEvento categoria = CategoriaEvento.builder()
                .evento(evento)
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .precioBase(dto.precioBase())
                .cargoServicio(dto.cargoServicio())
                .cupoMaximo(dto.cupoMaximo())
                .cuposDisponibles(dto.cupoMaximo())
                .edadMinima(dto.edadMinima())
                .edadMaxima(dto.edadMaxima())
                .incluyeKit(dto.incluyeKit() != null ? dto.incluyeKit() : false)
                .fechaInicioVenta(dto.fechaInicioVenta())
                .fechaFinVenta(dto.fechaFinVenta())
                .estado(EstadoCategoria.ACTIVO)
                .build();

        return mapToDTO(categoriaEventoRepository.save(categoria));
    }

    public CategoriaEventoResponseDTO obtenerPorId(Long id) {
        CategoriaEvento cat = categoriaEventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return mapToDTO(cat);
    }

    public List<CategoriaEventoResponseDTO> listarPorEvento(Long idEvento) {
        return categoriaEventoRepository.findByEventoIdEvento(idEvento).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CategoriaEventoResponseDTO mapToDTO(CategoriaEvento c) {
        return new CategoriaEventoResponseDTO(
                c.getIdCategoria(),
                c.getEvento() != null ? c.getEvento().getIdEvento() : null,
                c.getNombre(),
                c.getDescripcion(),
                c.getPrecioBase(),
                c.getCargoServicio(),
                c.getCupoMaximo(),
                c.getCuposDisponibles(),
                c.getEdadMinima(),
                c.getEdadMaxima(),
                c.getIncluyeKit(),
                c.getFechaInicioVenta(),
                c.getFechaFinVenta(),
                c.getEstado()
        );
    }
}
