package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.PagoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoTransaccionRepository extends JpaRepository<PagoTransaccion, Long> {

    List<PagoTransaccion> findByOrdenIdOrden(Long idOrden);

    Optional<PagoTransaccion> findByReferenciaExterna(String referenciaExterna);
}
