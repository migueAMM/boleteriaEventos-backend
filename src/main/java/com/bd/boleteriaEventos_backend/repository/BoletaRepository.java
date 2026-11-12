package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    long countByZonaIdZonaAndEstadoNot(Long idZona, String estado);
    long countByReservaPerfilClienteIdPerfilAndZonaFuncionIdFuncionAndEstadoNot(Long idPerfil, Long idFuncion, String estado);
    List<Boleta> findByZonaFuncionIdFuncion(Long idFuncion);
    Optional<Boleta> findByCodigo(String codigo);
    List<Boleta> findByReservaIdReserva(Long idReserva);
    List<Boleta> findByZonaIdZona(Long idZona);
    List<Boleta> findByComboIdCombo(Long idCombo);
}
