package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
    Optional<UsuarioSistema> findByCorreoIgnoreCase(String correo);
    List<UsuarioSistema> findByRolIdRol(Long idRol);
    List<UsuarioSistema> findByEstado(String estado);
}
