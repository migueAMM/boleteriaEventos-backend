package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdUsuario(Long idUsuario);
    List<Notificacion> findByUsuarioIdUsuarioAndLeidaFalse(Long idUsuario);
    List<Notificacion> findByReservaIdReserva(Long idReserva);
}
