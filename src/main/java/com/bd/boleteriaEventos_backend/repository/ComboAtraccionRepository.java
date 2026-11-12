package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.ComboAtraccion;
import com.bd.boleteriaEventos_backend.entity.ComboAtraccionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboAtraccionRepository extends JpaRepository<ComboAtraccion, ComboAtraccionId> {
    List<ComboAtraccion> findByIdIdCombo(Long idCombo);
    List<ComboAtraccion> findByIdIdAtraccion(Long idAtraccion);
}
