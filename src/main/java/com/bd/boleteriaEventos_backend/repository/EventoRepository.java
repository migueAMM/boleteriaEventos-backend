package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Evento;
import com.bd.boleteriaEventos_backend.enums.EstadoEvento;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    Optional<Evento> findBySlug(String slug);

    List<Evento> findByCiudadIgnoreCase(String ciudad);

    List<Evento> findByTipoEvento(TipoEvento tipoEvento);

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findByOrganizadorIdOrganizador(Long idOrganizador);

    @Query("SELECT e FROM Evento e WHERE e.fechaInicioEvento BETWEEN :start AND :end")
    List<Evento> findByFechaInicioEventoBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    List<Evento> findByCiudadIgnoreCaseAndTipoEvento(String ciudad, TipoEvento tipo);
}
