package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    @Modifying
    @Query("delete from Notificacion registro where registro.idNotificacion = :id")
    int eliminarSinCascada(@Param("id") Long id);

    List<Notificacion> findByUsuarioIdUsuario(Long idUsuario);
    List<Notificacion> findByUsuarioIdUsuarioAndLeidaFalse(Long idUsuario);
    List<Notificacion> findByReservaIdReserva(Long idReserva);
}
