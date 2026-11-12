package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.PerfilCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilClienteRepository extends JpaRepository<PerfilCliente, Long> {
    @Modifying
    @Query("delete from PerfilCliente registro where registro.idPerfil = :id")
    int eliminarSinCascada(@Param("id") Long id);

    Optional<PerfilCliente> findByDocumento(String documento);
    Optional<PerfilCliente> findByUsuarioIdUsuario(Long idUsuario);
}
