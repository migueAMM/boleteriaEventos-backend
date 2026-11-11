package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.TicketInscripcionResponseDTO;
import com.bd.boleteriaEventos_backend.entity.TicketInscripcion;
import com.bd.boleteriaEventos_backend.enums.EstadoTicket;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.TicketInscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketInscripcionRepository ticketInscripcionRepository;

    @Transactional
    public TicketInscripcionResponseDTO realizarCheckIn(String codigoQr) {
        TicketInscripcion ticket = ticketInscripcionRepository.findByCodigoTicketQr(codigoQr)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con código QR: " + codigoQr));

        if (Boolean.TRUE.equals(ticket.getCheckInRealizado())) {
            throw new BusinessRuleException("El check-in de este ticket ya fue realizado previamente el: " + ticket.getFechaCheckIn());
        }

        if (ticket.getEstadoTicket() != EstadoTicket.EMITIDO) {
            throw new BusinessRuleException("El ticket no puede ser utilizado. Estado actual: " + ticket.getEstadoTicket());
        }

        ticket.setCheckInRealizado(true);
        ticket.setFechaCheckIn(LocalDateTime.now());
        ticket.setEstadoTicket(EstadoTicket.UTILIZADO);

        TicketInscripcion actualizado = ticketInscripcionRepository.save(ticket);
        return mapToDTO(actualizado);
    }

    public TicketInscripcionResponseDTO obtenerPorCodigoQr(String codigoQr) {
        TicketInscripcion ticket = ticketInscripcionRepository.findByCodigoTicketQr(codigoQr)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con código QR: " + codigoQr));
        return mapToDTO(ticket);
    }

    public List<TicketInscripcionResponseDTO> listarPorOrden(Long idOrden) {
        return ticketInscripcionRepository.findByOrdenIdOrden(idOrden).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TicketInscripcionResponseDTO mapToDTO(TicketInscripcion t) {
        String nombreCategoria = t.getCategoria() != null ? t.getCategoria().getNombre() : null;
        Long idCategoria = t.getCategoria() != null ? t.getCategoria().getIdCategoria() : null;
        Long idOrden = t.getOrden() != null ? t.getOrden().getIdOrden() : null;

        return new TicketInscripcionResponseDTO(
                t.getIdTicket(),
                idOrden,
                idCategoria,
                nombreCategoria,
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
        );
    }
}
