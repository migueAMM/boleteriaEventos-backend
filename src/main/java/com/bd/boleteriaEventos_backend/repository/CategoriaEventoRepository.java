package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.CategoriaEvento;
import com.bd.boleteriaEventos_backend.enums.EstadoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaEventoRepository extends JpaRepository<CategoriaEvento, Long> {

    List<CategoriaEvento> findByEventoIdEvento(Long idEvento);

    List<CategoriaEvento> findByEventoIdEventoAndEstado(Long idEvento, EstadoCategoria estado);
}
