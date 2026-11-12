package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.TipoEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoEntradaRepository extends JpaRepository<TipoEntrada, Long> {
    Optional<TipoEntrada> findByNombreIgnoreCase(String nombre);
}
