package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Modifying
    @Query("delete from Plan registro where registro.idPlan = :id")
    int eliminarSinCascada(@Param("id") Long id);

    List<Plan> findByPrestadorIdPrestador(Long idPrestador);
    List<Plan> findByEstado(String estado);
}
