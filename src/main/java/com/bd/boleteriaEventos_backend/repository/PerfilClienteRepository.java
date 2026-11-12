package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.PerfilCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilClienteRepository extends JpaRepository<PerfilCliente, Long> {
    Optional<PerfilCliente> findByDocumento(String documento);
    Optional<PerfilCliente> findByUsuarioIdUsuario(Long idUsuario);
}
