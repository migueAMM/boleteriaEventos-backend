package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.TicketInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketInscripcionRepository extends JpaRepository<TicketInscripcion, Long> {

    Optional<TicketInscripcion> findByCodigoTicketQr(String codigoTicketQr);

    List<TicketInscripcion> findByOrdenIdOrden(Long idOrden);

    boolean existsByDocumentoAsistenteAndCategoriaEventoIdEvento(String documento, Long idEvento);

    @Query("SELECT MAX(t.numeroDorsal) FROM TicketInscripcion t WHERE t.categoria.evento.idEvento = :idEvento")
    Optional<Integer> findMaxNumeroDorsalByEventoId(@Param("idEvento") Long idEvento);
}
