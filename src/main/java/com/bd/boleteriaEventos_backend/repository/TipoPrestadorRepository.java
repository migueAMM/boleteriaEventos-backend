package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.TipoPrestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoPrestadorRepository extends JpaRepository<TipoPrestador, Long> {
    Optional<TipoPrestador> findByNombreIgnoreCase(String nombre);
}
