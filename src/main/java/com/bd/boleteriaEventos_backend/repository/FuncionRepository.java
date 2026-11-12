package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.dto.AforoFuncionDTO;
import com.bd.boleteriaEventos_backend.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    @Modifying
    @Query("delete from Funcion registro where registro.idFuncion = :id")
    int eliminarSinCascada(@Param("id") Long id);

    List<Funcion> findByAtraccionIdAtraccion(Long idAtraccion);
    List<Funcion> findByFecha(LocalDate fecha);

    @Query("""
            select new com.bd.boleteriaEventos_backend.dto.AforoFuncionDTO(
                f.idFuncion, a.nombre, f.fecha, f.horaApertura, f.horaCierre,
                coalesce((select sum(z.aforo) from Zona z where z.funcion = f), 0L),
                (select count(b) from Boleta b where b.zona.funcion = f and b.estado <> 'ANULADA'))
            from Funcion f join f.atraccion a
            order by f.fecha, f.horaApertura, f.idFuncion
            """)
    List<AforoFuncionDTO> reportarAforo();
}
