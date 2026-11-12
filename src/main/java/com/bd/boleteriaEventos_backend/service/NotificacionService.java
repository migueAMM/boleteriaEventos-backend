package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.NotificacionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.NotificacionResponseDTO;
import com.bd.boleteriaEventos_backend.dto.NotificacionUpdateDTO;
import com.bd.boleteriaEventos_backend.entity.Notificacion;
import com.bd.boleteriaEventos_backend.entity.Reserva;
import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.NotificacionRepository;
import com.bd.boleteriaEventos_backend.repository.ReservaRepository;
import com.bd.boleteriaEventos_backend.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public NotificacionResponseDTO crear(NotificacionCreateDTO dto) {
        UsuarioSistema usuario = usuarioSistemaRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.idUsuario()));

        Reserva reserva = null;
        if (dto.idReserva() != null) {
            reserva = reservaRepository.findById(dto.idReserva())
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + dto.idReserva()));
        }

        return mapToDTO(guardar(usuario, reserva, dto.asunto().trim(), dto.mensaje().trim(), LocalDateTime.now()));
    }

    @Transactional
    public void notificarReserva(Reserva reserva, String asunto, String mensaje) {
        guardar(reserva.getPerfilCliente().getUsuario(), reserva, asunto, mensaje, BoletaService.ahora());
    }

    private Notificacion guardar(UsuarioSistema usuario, Reserva reserva, String asunto, String mensaje, LocalDateTime fechaHora) {
        Notificacion notificacion = Notificacion.builder()
                .asunto(asunto)
                .mensaje(mensaje)
                .fechaHora(fechaHora)
                .leida(false)
                .usuario(usuario)
                .reserva(reserva)
                .build();

        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public NotificacionResponseDTO actualizar(Long id, NotificacionUpdateDTO dto) {
        Notificacion registro = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrado con ID: " + id));
        registro.setAsunto(dto.asunto().trim());
        registro.setMensaje(dto.mensaje().trim());
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            if (notificacionRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Notificación no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
    }

    public List<NotificacionResponseDTO> listarTodos() {
        return notificacionRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public NotificacionResponseDTO obtenerPorId(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        return mapToDTO(n);
    }

    public List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario) {
        return notificacionRepository.findByUsuarioIdUsuario(idUsuario).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public NotificacionResponseDTO marcarComoLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        n.setLeida(true);
        return mapToDTO(n);
    }

    public NotificacionResponseDTO mapToDTO(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getIdNotificacion(),
                n.getAsunto(),
                n.getMensaje(),
                n.getFechaHora(),
                n.getLeida(),
                n.getUsuario() != null ? n.getUsuario().getIdUsuario() : null,
                n.getReserva() != null ? n.getReserva().getIdReserva() : null
        );
    }
}
