package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {

    Optional<Organizador> findByNit(String nit);

    Optional<Organizador> findByUsuarioIdUsuario(Long idUsuario);
}
