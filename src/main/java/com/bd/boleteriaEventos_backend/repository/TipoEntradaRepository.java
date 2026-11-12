package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.TipoEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoEntradaRepository extends JpaRepository<TipoEntrada, Long> {
    @Modifying
    @Query("delete from TipoEntrada registro where registro.idTipoEntrada = :id")
    int eliminarSinCascada(@Param("id") Long id);

    Optional<TipoEntrada> findByNombreIgnoreCase(String nombre);
}
