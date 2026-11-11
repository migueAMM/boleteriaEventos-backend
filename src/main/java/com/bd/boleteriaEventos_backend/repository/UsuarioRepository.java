package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Usuario;
import com.bd.boleteriaEventos_backend.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    List<Usuario> findByRol(Rol rol);
}
