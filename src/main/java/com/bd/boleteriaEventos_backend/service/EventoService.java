package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.CategoriaEventoResponseDTO;
import com.bd.boleteriaEventos_backend.dto.EventoCreateDTO;
import com.bd.boleteriaEventos_backend.dto.EventoResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Evento;
import com.bd.boleteriaEventos_backend.entity.Organizador;
import com.bd.boleteriaEventos_backend.enums.EstadoEvento;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.CategoriaEventoRepository;
import com.bd.boleteriaEventos_backend.repository.EventoRepository;
import com.bd.boleteriaEventos_backend.repository.OrganizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventoService {

    private final EventoRepository eventoRepository;
    private final OrganizadorRepository organizadorRepository;
    private final CategoriaEventoRepository categoriaEventoRepository;

    @Transactional
    public EventoResponseDTO crear(EventoCreateDTO dto) {
        Organizador organizador = organizadorRepository.findById(dto.idOrganizador())
                .orElseThrow(() -> new ResourceNotFoundException("Organizador no encontrado con ID: " + dto.idOrganizador()));

        String slug = generateSlug(dto.titulo());

        Evento evento = Evento.builder()
                .organizador(organizador)
                .titulo(dto.titulo())
                .slug(slug)
                .descripcion(dto.descripcion())
                .tipoEvento(dto.tipoEvento())
                .ciudad(dto.ciudad())
                .direccion(dto.direccion())
                .lugarNombre(dto.lugarNombre())
                .fechaInicioEvento(dto.fechaInicioEvento())
                .fechaFinEvento(dto.fechaFinEvento())
                .fechaAperturaInscripciones(dto.fechaAperturaInscripciones())
                .fechaCierreInscripciones(dto.fechaCierreInscripciones())
                .imagenBannerUrl(dto.imagenBannerUrl())
                .aforoMaximoTotal(dto.aforoMaximoTotal())
                .estado(EstadoEvento.BORRADOR)
                .build();

        return mapToDTO(eventoRepository.save(evento));
    }

    public EventoResponseDTO obtenerPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));
        return mapToDTO(evento);
    }

    public EventoResponseDTO obtenerPorSlug(String slug) {
        Evento evento = eventoRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con slug: " + slug));
        return mapToDTO(evento);
    }

    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<EventoResponseDTO> filtrar(String ciudad, TipoEvento tipo, LocalDateTime start, LocalDateTime end) {
        if (ciudad != null && !ciudad.isBlank() && tipo != null) {
            return eventoRepository.findByCiudadIgnoreCaseAndTipoEvento(ciudad, tipo).stream().map(this::mapToDTO).toList();
        }
        if (ciudad != null && !ciudad.isBlank()) {
            return eventoRepository.findByCiudadIgnoreCase(ciudad).stream().map(this::mapToDTO).toList();
        }
        if (tipo != null) {
            return eventoRepository.findByTipoEvento(tipo).stream().map(this::mapToDTO).toList();
        }
        if (start != null && end != null) {
            return eventoRepository.findByFechaInicioEventoBetween(start, end).stream().map(this::mapToDTO).toList();
        }
        return listarTodos();
    }

    @Transactional
    public EventoResponseDTO publicar(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        var categorias = categoriaEventoRepository.findByEventoIdEvento(id);
        if (categorias.isEmpty()) {
            throw new BusinessRuleException("No se puede publicar un evento sin categorías o localidades configuradas");
        }

        evento.setEstado(EstadoEvento.PUBLICADO);
        return mapToDTO(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponseDTO cancelar(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        evento.setEstado(EstadoEvento.CANCELADO);
        return mapToDTO(eventoRepository.save(evento));
    }

    private String generateSlug(String input) {
        String nonLatin = "[^\\w\\s]";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = Pattern.compile(nonLatin).matcher(normalized).replaceAll("")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase(Locale.ENGLISH);

        String randomSuffix = UUID.randomUUID().toString().substring(0, 6);
        String finalSlug = slug + "-" + randomSuffix;

        while (eventoRepository.findBySlug(finalSlug).isPresent()) {
            finalSlug = slug + "-" + UUID.randomUUID().toString().substring(0, 6);
        }
        return finalSlug;
    }

    public EventoResponseDTO mapToDTO(Evento e) {
        String nombreOrganizador = e.getOrganizador() != null
                ? e.getOrganizador().getNombreComercial() != null ? e.getOrganizador().getNombreComercial() : e.getOrganizador().getRazonSocial()
                : null;
        Long idOrganizador = e.getOrganizador() != null ? e.getOrganizador().getIdOrganizador() : null;

        List<CategoriaEventoResponseDTO> catDTOs = e.getCategorias() != null
                ? e.getCategorias().stream().map(c -> new CategoriaEventoResponseDTO(
                        c.getIdCategoria(),
                        e.getIdEvento(),
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
                )).toList()
                : Collections.emptyList();

        return new EventoResponseDTO(
                e.getIdEvento(),
                idOrganizador,
                nombreOrganizador,
                e.getTitulo(),
                e.getSlug(),
                e.getDescripcion(),
                e.getTipoEvento(),
                e.getCiudad(),
                e.getDireccion(),
                e.getLugarNombre(),
                e.getFechaInicioEvento(),
                e.getFechaFinEvento(),
                e.getFechaAperturaInscripciones(),
                e.getFechaCierreInscripciones(),
                e.getImagenBannerUrl(),
                e.getAforoMaximoTotal(),
                e.getEstado(),
                catDTOs
        );
    }
}
