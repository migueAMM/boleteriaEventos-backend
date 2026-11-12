package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.NotificacionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.NotificacionResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Notificacion;
import com.bd.boleteriaEventos_backend.entity.Reserva;
import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.NotificacionRepository;
import com.bd.boleteriaEventos_backend.repository.ReservaRepository;
import com.bd.boleteriaEventos_backend.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
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

        Notificacion notificacion = Notificacion.builder()
                .asunto(dto.asunto().trim())
                .mensaje(dto.mensaje().trim())
                .fechaHora(LocalDateTime.now())
                .leida(false)
                .usuario(usuario)
                .reserva(reserva)
                .build();

        return mapToDTO(notificacionRepository.save(notificacion));
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
