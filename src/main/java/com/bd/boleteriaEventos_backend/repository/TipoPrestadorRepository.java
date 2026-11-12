package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.TipoPrestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoPrestadorRepository extends JpaRepository<TipoPrestador, Long> {
    @Modifying
    @Query("delete from TipoPrestador r where r.idTipoPrestador = :id")
    int eliminarSinCascada(@Param("id") Long id);

    Optional<TipoPrestador> findByNombreIgnoreCase(String nombre);
}
