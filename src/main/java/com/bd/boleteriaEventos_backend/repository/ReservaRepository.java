package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Modifying
    @Query("delete from Reserva registro where registro.idReserva = :id")
    int eliminarSinCascada(@Param("id") Long id);

    List<Reserva> findByPerfilClienteIdPerfil(Long idPerfil);
    List<Reserva> findByPlanIdPlan(Long idPlan);
    boolean existsByEstadoAndFechaHoraLessThanEqual(String estado, LocalDateTime fechaLimite);
    List<Reserva> findByEstadoAndFechaHoraLessThanEqual(String estado, LocalDateTime fechaLimite);
}
