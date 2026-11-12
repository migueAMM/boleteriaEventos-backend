package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long> {
    @Modifying
    @Query("delete from Zona registro where registro.idZona = :id")
    int eliminarSinCascada(@Param("id") Long id);

    List<Zona> findByFuncionIdFuncion(Long idFuncion);
}
