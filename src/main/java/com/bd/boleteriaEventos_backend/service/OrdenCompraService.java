package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.OrdenCompraCreateDTO;
import com.bd.boleteriaEventos_backend.dto.OrdenCompraResponseDTO;
import com.bd.boleteriaEventos_backend.dto.TicketInscripcionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.TicketInscripcionResponseDTO;
import com.bd.boleteriaEventos_backend.entity.*;
import com.bd.boleteriaEventos_backend.enums.EstadoCategoria;
import com.bd.boleteriaEventos_backend.enums.EstadoEvento;
import com.bd.boleteriaEventos_backend.enums.EstadoOrden;
import com.bd.boleteriaEventos_backend.enums.EstadoTicket;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final CategoriaEventoRepository categoriaEventoRepository;
    private final CuponDescuentoRepository cuponDescuentoRepository;
    private final TicketInscripcionRepository ticketInscripcionRepository;

    @Transactional
    public OrdenCompraResponseDTO crearOrden(OrdenCompraCreateDTO dto) {
        // 1. Validar comprador
        Usuario comprador = usuarioRepository.findById(dto.idComprador())
                .orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado con ID: " + dto.idComprador()));

        if (!Boolean.TRUE.equals(comprador.getActivo())) {
            throw new BusinessRuleException("El comprador se encuentra inactivo en la plataforma");
        }

        // 2. Validar evento
        Evento evento = eventoRepository.findById(dto.idEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + dto.idEvento()));

        if (evento.getEstado() != EstadoEvento.PUBLICADO) {
            throw new BusinessRuleException("El evento no está disponible para compras. Estado actual: " + evento.getEstado());
        }

        LocalDateTime now = LocalDateTime.now();
        if (evento.getFechaAperturaInscripciones() != null && now.isBefore(evento.getFechaAperturaInscripciones())) {
            throw new BusinessRuleException("Las inscripciones para este evento aún no están abiertas");
        }
        if (evento.getFechaCierreInscripciones() != null && now.isAfter(evento.getFechaCierreInscripciones())) {
            throw new BusinessRuleException("Las inscripciones para este evento ya han finalizado");
        }

        if (dto.tickets() == null || dto.tickets().isEmpty()) {
            throw new BusinessRuleException("Debe seleccionar al menos un ticket para procesar la orden");
        }

        // Para evitar duplicados de documento en eventos deportivos dentro de la misma orden
        Set<String> documentosEnOrden = new HashSet<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalCargosServicio = BigDecimal.ZERO;
        List<TicketInscripcion> ticketsParaGuardar = new ArrayList<>();

        // 3. Procesar y validar cada ticket
        for (TicketInscripcionCreateDTO tDto : dto.tickets()) {
            CategoriaEvento categoria = categoriaEventoRepository.findById(tDto.idCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + tDto.idCategoria()));

            if (!categoria.getEvento().getIdEvento().equals(evento.getIdEvento())) {
                throw new BusinessRuleException("La categoría '" + categoria.getNombre() + "' no pertenece al evento seleccionado");
            }

            if (categoria.getEstado() != EstadoCategoria.ACTIVO) {
                throw new BusinessRuleException("La categoría '" + categoria.getNombre() + "' no está activa para la venta");
            }

            if (categoria.getFechaInicioVenta() != null && now.isBefore(categoria.getFechaInicioVenta())) {
                throw new BusinessRuleException("La venta para la categoría '" + categoria.getNombre() + "' aún no inicia");
            }
            if (categoria.getFechaFinVenta() != null && now.isAfter(categoria.getFechaFinVenta())) {
                throw new BusinessRuleException("La venta para la categoría '" + categoria.getNombre() + "' ya finalizó");
            }

            // Control de stock
            if (categoria.getCuposDisponibles() == null || categoria.getCuposDisponibles() <= 0) {
                throw new BusinessRuleException("No hay cupos disponibles en la categoría: " + categoria.getNombre());
            }

            // Validaciones para evento deportivo
            if (evento.getTipoEvento() == TipoEvento.DEPORTIVO) {
                String doc = tDto.documentoAsistente().trim();

                if (!documentosEnOrden.add(doc)) {
                    throw new BusinessRuleException("El documento " + doc + " está duplicado dentro de la misma orden");
                }

                boolean yaInscrito = ticketInscripcionRepository
                        .existsByDocumentoAsistenteAndCategoriaEventoIdEvento(doc, evento.getIdEvento());
                if (yaInscrito) {
                    throw new BusinessRuleException("El asistente con documento " + doc + " ya se encuentra inscrito en este evento deportivo");
                }

                // Validación de edad si aplica
                if (tDto.fechaNacimientoAsistente() != null) {
                    int edad = Period.between(tDto.fechaNacimientoAsistente(), LocalDate.now()).getYears();
                    if (categoria.getEdadMinima() != null && edad < categoria.getEdadMinima()) {
                        throw new BusinessRuleException("El asistente no cumple con la edad mínima requerida (" +
                                categoria.getEdadMinima() + " años) para la categoría " + categoria.getNombre());
                    }
                    if (categoria.getEdadMaxima() != null && edad > categoria.getEdadMaxima()) {
                        throw new BusinessRuleException("El asistente excede la edad máxima permitida (" +
                                categoria.getEdadMaxima() + " años) para la categoría " + categoria.getNombre());
                    }
                }
            }

            // Descontar cupo
            categoria.setCuposDisponibles(categoria.getCuposDisponibles() - 1);
            if (categoria.getCuposDisponibles() == 0) {
                categoria.setEstado(EstadoCategoria.AGOTADO);
            }
            categoriaEventoRepository.save(categoria);

            // Sumar montos
            subtotal = subtotal.add(categoria.getPrecioBase());
            totalCargosServicio = totalCargosServicio.add(categoria.getCargoServicio());

            // Preparar ticket
            BigDecimal precioTicket = categoria.getPrecioBase().add(categoria.getCargoServicio());
            TicketInscripcion ticket = TicketInscripcion.builder()
                    .categoria(categoria)
                    .codigoTicketQr("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .tipoDocumentoAsistente(tDto.tipoDocumentoAsistente())
                    .documentoAsistente(tDto.documentoAsistente().trim())
                    .nombreCompletoAsistente(tDto.nombreCompletoAsistente().trim())
                    .emailAsistente(tDto.emailAsistente().trim().toLowerCase())
                    .telefonoAsistente(tDto.telefonoAsistente())
                    .tallaCamiseta(tDto.tallaCamiseta())
                    .contactoEmergenciaNombre(tDto.contactoEmergenciaNombre())
                    .contactoEmergenciaTelefono(tDto.contactoEmergenciaTelefono())
                    .tipoSangre(tDto.tipoSangre())
                    .precioUnitarioPagado(precioTicket)
                    .checkInRealizado(false)
                    .estadoTicket(EstadoTicket.RESERVADO)
                    .build();

            ticketsParaGuardar.add(ticket);
        }

        // 4. Manejo de cupón de descuento
        CuponDescuento cuponAplicado = null;
        BigDecimal totalDescuento = BigDecimal.ZERO;

        if (dto.codigoCupon() != null && !dto.codigoCupon().isBlank()) {
            String codigo = dto.codigoCupon().trim().toUpperCase();
            cuponAplicado = cuponDescuentoRepository.findByEventoIdEventoAndCodigo(evento.getIdEvento(), codigo)
                    .orElseThrow(() -> new BusinessRuleException("Cupón '" + codigo + "' inválido para este evento"));

            if (!Boolean.TRUE.equals(cuponAplicado.getActivo())) {
                throw new BusinessRuleException("El cupón '" + codigo + "' no está activo");
            }
            if (cuponAplicado.getFechaExpiracion() != null && cuponAplicado.getFechaExpiracion().isBefore(now)) {
                throw new BusinessRuleException("El cupón '" + codigo + "' ha expirado");
            }
            if (cuponAplicado.getUsosMaximos() != null && cuponAplicado.getUsosActuales() >= cuponAplicado.getUsosMaximos()) {
                throw new BusinessRuleException("El cupón '" + codigo + "' ya agotó su límite de usos");
            }

            // Aplicar descuento sobre el subtotal
            totalDescuento = subtotal.multiply(cuponAplicado.getPorcentajeDescuento())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            cuponAplicado.setUsosActuales(cuponAplicado.getUsosActuales() + 1);
            cuponDescuentoRepository.save(cuponAplicado);
        }

        // 5. Cálculo total
        BigDecimal montoTotal = subtotal.add(totalCargosServicio).subtract(totalDescuento);
        if (montoTotal.compareTo(BigDecimal.ZERO) < 0) {
            montoTotal = BigDecimal.ZERO;
        }

        // 6. Crear orden
        String codigoOrden = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        OrdenCompra orden = OrdenCompra.builder()
                .codigoOrden(codigoOrden)
                .comprador(comprador)
                .evento(evento)
                .cupon(cuponAplicado)
                .subtotal(subtotal)
                .totalCargosServicio(totalCargosServicio)
                .totalDescuento(totalDescuento)
                .montoTotal(montoTotal)
                .estadoOrden(EstadoOrden.PENDIENTE_PAGO)
                .fechaCreacion(now)
                .fechaExpiracionReserva(now.plusMinutes(30))
                .build();

        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);

        // Asociar tickets a la orden y guardar
        for (TicketInscripcion ticket : ticketsParaGuardar) {
            ticket.setOrden(ordenGuardada);
        }
        List<TicketInscripcion> ticketsGuardados = ticketInscripcionRepository.saveAll(ticketsParaGuardar);
        ordenGuardada.setTickets(ticketsGuardados);

        return mapToDTO(ordenGuardada);
    }

    public OrdenCompraResponseDTO obtenerPorId(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return mapToDTO(orden);
    }

    public OrdenCompraResponseDTO obtenerPorCodigo(String codigo) {
        OrdenCompra orden = ordenCompraRepository.findByCodigoOrden(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con código: " + codigo));
        return mapToDTO(orden);
    }

    public List<OrdenCompraResponseDTO> listarPorComprador(Long idComprador) {
        return ordenCompraRepository.findByCompradorIdUsuario(idComprador).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public OrdenCompraResponseDTO mapToDTO(OrdenCompra o) {
        String nombreComprador = o.getComprador() != null
                ? o.getComprador().getNombres() + " " + o.getComprador().getApellidos()
                : null;
        Long idComprador = o.getComprador() != null ? o.getComprador().getIdUsuario() : null;

        String tituloEvento = o.getEvento() != null ? o.getEvento().getTitulo() : null;
        Long idEvento = o.getEvento() != null ? o.getEvento().getIdEvento() : null;

        String codigoCupon = o.getCupon() != null ? o.getCupon().getCodigo() : null;

        List<TicketInscripcionResponseDTO> ticketDTOs = o.getTickets() != null
                ? o.getTickets().stream().map(t -> new TicketInscripcionResponseDTO(
                        t.getIdTicket(),
                        o.getIdOrden(),
                        t.getCategoria() != null ? t.getCategoria().getIdCategoria() : null,
                        t.getCategoria() != null ? t.getCategoria().getNombre() : null,
                        t.getCodigoTicketQr(),
                        t.getTipoDocumentoAsistente(),
                        t.getDocumentoAsistente(),
                        t.getNombreCompletoAsistente(),
                        t.getEmailAsistente(),
                        t.getTelefonoAsistente(),
                        t.getTallaCamiseta(),
                        t.getContactoEmergenciaNombre(),
                        t.getContactoEmergenciaTelefono(),
                        t.getTipoSangre(),
                        t.getNumeroDorsal(),
                        t.getPrecioUnitarioPagado(),
                        t.getCheckInRealizado(),
                        t.getFechaCheckIn(),
                        t.getEstadoTicket()
                )).toList()
                : Collections.emptyList();

        return new OrdenCompraResponseDTO(
                o.getIdOrden(),
                o.getCodigoOrden(),
                idComprador,
                nombreComprador,
                idEvento,
                tituloEvento,
                codigoCupon,
                o.getSubtotal(),
                o.getTotalCargosServicio(),
                o.getTotalDescuento(),
                o.getMontoTotal(),
                o.getEstadoOrden(),
                o.getFechaCreacion(),
                o.getFechaExpiracionReserva(),
                ticketDTOs
        );
    }
}
