package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
    @Modifying
    @Query("delete from UsuarioSistema registro where registro.idUsuario = :id")
    int eliminarSinCascada(@Param("id") Long id);

    Optional<UsuarioSistema> findByCorreoIgnoreCase(String correo);
    List<UsuarioSistema> findByRolIdRol(Long idRol);
    List<UsuarioSistema> findByEstado(String estado);
}
