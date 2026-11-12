package com.bd.boleteriaEventos_backend.repository;

import com.bd.boleteriaEventos_backend.dto.ComboVentasDTO;
import com.bd.boleteriaEventos_backend.dto.IngresosAtraccionDTO;
import com.bd.boleteriaEventos_backend.entity.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    @Modifying
    @Query("delete from Boleta registro where registro.idBoleta = :id")
    int eliminarSinCascada(@Param("id") Long id);

    long countByZonaIdZonaAndEstadoNot(Long idZona, String estado);
    long countByReservaPerfilClienteIdPerfilAndZonaFuncionIdFuncionAndEstadoNot(Long idPerfil, Long idFuncion, String estado);
    List<Boleta> findByZonaFuncionIdFuncion(Long idFuncion);
    boolean existsByReservaIdReserva(Long idReserva);
    boolean existsByZonaIdZona(Long idZona);
    boolean existsByZonaFuncionIdFuncion(Long idFuncion);
    boolean existsByComboIdCombo(Long idCombo);
    Optional<Boleta> findByCodigo(String codigo);
    List<Boleta> findByReservaIdReserva(Long idReserva);
    List<Boleta> findByZonaIdZona(Long idZona);
    List<Boleta> findByComboIdCombo(Long idCombo);

    @Query("""
            select new com.bd.boleteriaEventos_backend.dto.IngresosAtraccionDTO(
                a.idAtraccion, a.nombre, t.idTipoEntrada, t.nombre, count(b), sum(b.precioPagado))
            from Boleta b join b.zona z join z.funcion f join f.atraccion a
            left join b.tipoEntrada t
            where b.reserva.estado = :estadoReserva and b.estado in :estadosBoleta
            group by a.idAtraccion, a.nombre, t.idTipoEntrada, t.nombre
            order by a.idAtraccion, t.idTipoEntrada
            """)
    List<IngresosAtraccionDTO> reportarIngresos(
            @Param("estadoReserva") String estadoReserva,
            @Param("estadosBoleta") List<String> estadosBoleta);

    @Query("""
            select new com.bd.boleteriaEventos_backend.dto.ComboVentasDTO(
                c.idCombo, c.nombre, count(distinct b.reserva.idReserva), sum(b.precioPagado))
            from Boleta b join b.combo c
            where b.reserva.estado = :estadoReserva and b.estado in :estadosBoleta
            group by c.idCombo, c.nombre
            order by count(distinct b.reserva.idReserva) desc, c.idCombo
            """)
    List<ComboVentasDTO> reportarCombos(
            @Param("estadoReserva") String estadoReserva,
            @Param("estadosBoleta") List<String> estadosBoleta);
}
