package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.*;
import com.bd.boleteriaEventos_backend.entity.Combo;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.ComboRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "BOLETERIA_PRUEBA_ORACLE", matches = "true")
class CrudOracleTest {

    @Autowired private MunicipioService municipios;
    @Autowired private TipoPrestadorService tiposPrestador;
    @Autowired private PrestadorService prestadores;
    @Autowired private PlanService planes;
    @Autowired private AtraccionService atracciones;
    @Autowired private FuncionService funciones;
    @Autowired private ZonaService zonas;
    @Autowired private ComboRepository combos;
    @Autowired private ComboAtraccionService enlaces;
    @Autowired private EntityManager entityManager;

    @Test
    void actualizaYEliminaPlanDePrueba() {
        Long idPrestador = crearPrestador();
        Long id = planes.crear(new PlanCreateDTO("Prueba", null, "ACTIVO", idPrestador)).idPlan();

        planes.actualizar(id, new PlanCreateDTO("Prueba actualizada", "Temporal", "ACTIVO", idPrestador));
        entityManager.flush();
        entityManager.clear();
        assertEquals("Prueba actualizada", planes.obtenerPorId(id).nombre());

        planes.eliminar(id);
        entityManager.clear();
        assertThrows(ResourceNotFoundException.class, () -> planes.obtenerPorId(id));
    }

    @Test
    void oracleProtegeLaFuncionConZonasYLeeSuHorarioActualizado() {
        Long idAtraccion = crearAtraccion();
        var fecha = BoletaService.ahora().toLocalDate().plusDays(5);
        Long id = funciones.crear(new FuncionCreateDTO(fecha, LocalTime.of(10, 0), LocalTime.NOON,
                "PROGRAMADA", idAtraccion)).idFuncion();
        funciones.actualizar(id, new FuncionUpdateDTO(fecha, LocalTime.of(10, 30), LocalTime.NOON, idAtraccion));
        entityManager.flush();
        entityManager.clear();
        assertEquals(LocalTime.of(10, 30), funciones.obtenerPorId(id).horaApertura());

        zonas.crear(new ZonaCreateDTO("Prueba", 10, BigDecimal.TEN, id));
        entityManager.flush();
        assertThrows(BusinessRuleException.class, () -> funciones.eliminar(id));
    }

    @Test
    void oraclePersisteYConsultaLaAsociacionPorClaveCompuesta() {
        Long idAtraccion = crearAtraccion();
        Long idCombo = combos.save(Combo.builder().nombre("Temporal").precio(BigDecimal.TEN)
                .diasVigencia(5).estado("ACTIVO").build()).getIdCombo();
        enlaces.crear(idCombo, idAtraccion, new BigDecimal("100.00"));
        entityManager.flush();
        entityManager.clear();

        var enlace = enlaces.obtenerPorId(idCombo, idAtraccion);
        assertEquals(idCombo, enlace.idCombo());
        assertEquals(idAtraccion, enlace.idAtraccion());
        assertEquals(0, new BigDecimal("100").compareTo(enlace.porcentajePrecio()));
    }

    private Long crearAtraccion() {
        return atracciones.crear(new AtraccionCreateDTO("Prueba", null, crearPrestador())).idAtraccion();
    }

    private Long crearPrestador() {
        Long municipio = municipios.crear(new MunicipioCreateDTO("Temporal", "Prueba")).idMunicipio();
        Long tipo = tiposPrestador.crear(new TipoPrestadorCreateDTO("Temporal")).idTipoPrestador();
        String nit = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        return prestadores.crear(new PrestadorCreateDTO(nit, "Prueba", "prueba@ejemplo.com",
                municipio, tipo)).idPrestador();
    }
}
