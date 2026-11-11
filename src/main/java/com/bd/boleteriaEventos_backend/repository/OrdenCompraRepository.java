package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    Optional<OrdenCompra> findByCodigoOrden(String codigoOrden);

    List<OrdenCompra> findByCompradorIdUsuario(Long idComprador);

    List<OrdenCompra> findByEventoIdEvento(Long idEvento);
}
