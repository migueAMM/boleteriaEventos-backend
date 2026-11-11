package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.CuponDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuponDescuentoRepository extends JpaRepository<CuponDescuento, Long> {

    Optional<CuponDescuento> findByEventoIdEventoAndCodigo(Long idEvento, String codigo);

    List<CuponDescuento> findByEventoIdEvento(Long idEvento);
}
