package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.PagoTransaccionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.PagoTransaccionResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Evento;
import com.bd.boleteriaEventos_backend.entity.OrdenCompra;
import com.bd.boleteriaEventos_backend.entity.PagoTransaccion;
import com.bd.boleteriaEventos_backend.entity.TicketInscripcion;
import com.bd.boleteriaEventos_backend.enums.EstadoOrden;
import com.bd.boleteriaEventos_backend.enums.EstadoPago;
import com.bd.boleteriaEventos_backend.enums.EstadoTicket;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.OrdenCompraRepository;
import com.bd.boleteriaEventos_backend.repository.PagoTransaccionRepository;
import com.bd.boleteriaEventos_backend.repository.TicketInscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PagoTransaccionService {

    private final PagoTransaccionRepository pagoTransaccionRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final TicketInscripcionRepository ticketInscripcionRepository;

    @Transactional
    public PagoTransaccionResponseDTO registrarPago(PagoTransaccionCreateDTO dto) {
        OrdenCompra orden = ordenCompraRepository.findById(dto.idOrden())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + dto.idOrden()));

        if (orden.getEstadoOrden() == EstadoOrden.PAGADA) {
            throw new BusinessRuleException("La orden ya se encuentra pagada");
        }
        if (orden.getEstadoOrden() == EstadoOrden.CANCELADA || orden.getEstadoOrden() == EstadoOrden.EXPIRADA) {
            throw new BusinessRuleException("No se puede pagar una orden en estado: " + orden.getEstadoOrden());
        }

        if (dto.monto().compareTo(orden.getMontoTotal()) < 0) {
            throw new BusinessRuleException("El monto a pagar (" + dto.monto() + ") es menor al total de la orden (" + orden.getMontoTotal() + ")");
        }

        PagoTransaccion pago = PagoTransaccion.builder()
                .orden(orden)
                .pasarelaPago(dto.pasarelaPago())
                .referenciaExterna(dto.referenciaExterna())
                .metodoPago(dto.metodoPago())
                .monto(dto.monto())
                .estadoPago(EstadoPago.APROBADO)
                .fechaPago(LocalDateTime.now())
                .build();

        PagoTransaccion guardado = pagoTransaccionRepository.save(pago);

        // Procesar aprobación y emisión inmediata
        procesarAprobacionOrden(orden);

        return mapToDTO(guardado);
    }

    @Transactional
    public PagoTransaccionResponseDTO confirmarPago(Long idPago) {
        PagoTransaccion pago = pagoTransaccionRepository.findById(idPago)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + idPago));

        pago.setEstadoPago(EstadoPago.APROBADO);
        pago.setFechaPago(LocalDateTime.now());
        PagoTransaccion pagoActualizado = pagoTransaccionRepository.save(pago);

        procesarAprobacionOrden(pago.getOrden());

        return mapToDTO(pagoActualizado);
    }

    private void procesarAprobacionOrden(OrdenCompra orden) {
        orden.setEstadoOrden(EstadoOrden.PAGADA);
        ordenCompraRepository.save(orden);

        Evento evento = orden.getEvento();
        boolean esDeportivo = evento != null && evento.getTipoEvento() == TipoEvento.DEPORTIVO;

        // Determinar siguiente dorsal si es evento deportivo
        int proximoDorsal = 1;
        if (esDeportivo && evento.getIdEvento() != null) {
            proximoDorsal = ticketInscripcionRepository.findMaxNumeroDorsalByEventoId(evento.getIdEvento())
                    .map(max -> max + 1)
                    .orElse(1);
        }

        List<TicketInscripcion> tickets = ticketInscripcionRepository.findByOrdenIdOrden(orden.getIdOrden());
        for (TicketInscripcion ticket : tickets) {
            ticket.setEstadoTicket(EstadoTicket.EMITIDO);
            ticket.setCodigoTicketQr("QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());

            if (esDeportivo && ticket.getNumeroDorsal() == null) {
                ticket.setNumeroDorsal(proximoDorsal++);
            }
        }
        ticketInscripcionRepository.saveAll(tickets);
    }

    public List<PagoTransaccionResponseDTO> listarPorOrden(Long idOrden) {
        return pagoTransaccionRepository.findByOrdenIdOrden(idOrden).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PagoTransaccionResponseDTO mapToDTO(PagoTransaccion p) {
        String codigoOrden = p.getOrden() != null ? p.getOrden().getCodigoOrden() : null;
        Long idOrden = p.getOrden() != null ? p.getOrden().getIdOrden() : null;

        return new PagoTransaccionResponseDTO(
                p.getIdPago(),
                idOrden,
                codigoOrden,
                p.getPasarelaPago(),
                p.getReferenciaExterna(),
                p.getMetodoPago(),
                p.getMonto(),
                p.getEstadoPago(),
                p.getFechaPago()
        );
    }
}
