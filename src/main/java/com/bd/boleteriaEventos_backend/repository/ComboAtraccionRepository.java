package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.ComboAtraccion;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboAtraccionRepository extends JpaRepository<ComboAtraccion, ComboAtraccionId> {
    @Modifying
    @Query("delete from ComboAtraccion ca where ca.id.idCombo = :idCombo")
    void eliminarPorCombo(@Param("idCombo") Long idCombo);

    List<ComboAtraccion> findByIdIdCombo(Long idCombo);
    List<ComboAtraccion> findByIdIdAtraccion(Long idAtraccion);
}
