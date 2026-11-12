package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Long> {
    @Modifying
    @Query("delete from Combo c where c.idCombo = :id")
    void eliminarSinCascada(@Param("id") Long id);

    List<Combo> findByEstado(String estado);
}
