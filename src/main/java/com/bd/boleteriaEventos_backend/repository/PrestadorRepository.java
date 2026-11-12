package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
    Optional<Prestador> findByNit(String nit);
    Optional<Prestador> findByCorreo(String correo);
    List<Prestador> findByMunicipioIdMunicipio(Long idMunicipio);
    List<Prestador> findByTipoPrestadorIdTipoPrestador(Long idTipoPrestador);
}
