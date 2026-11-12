package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.controller.PlanController;
import com.bd.boleteriaEventos_backend.dto.FuncionUpdateDTO;
import com.bd.boleteriaEventos_backend.dto.ReservaUpdateDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaUpdateDTO;
import com.bd.boleteriaEventos_backend.dto.ZonaCreateDTO;
import com.bd.boleteriaEventos_backend.entity.*;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.GlobalExceptionHandler;
import com.bd.boleteriaEventos_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CrudPendientesTest {

    private final BloqueoBoleteria bloqueo = mock(BloqueoBoleteria.class);
    private final BoletaRepository boletas = mock(BoletaRepository.class);

    @Test
    void planExponeActualizacionValidadaYEliminacionSinCascada() throws Exception {
        var planes = mock(PlanRepository.class);
        var prestadores = mock(PrestadorRepository.class);
        var plan = Plan.builder().idPlan(1L).build();
        when(planes.findById(1L)).thenReturn(Optional.of(plan));
        when(prestadores.findById(2L)).thenReturn(Optional.of(Prestador.builder().idPrestador(2L).build()));
        when(planes.eliminarSinCascada(1L)).thenReturn(1);
        when(planes.eliminarSinCascada(3L)).thenThrow(new DataIntegrityViolationException("FK"));
        var api = MockMvcBuilders.standaloneSetup(new PlanController(new PlanService(planes, prestadores)))
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        api.perform(put("/planes/1").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Visita","estado":"ACTIVO","idPrestador":2}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Visita"));
        api.perform(put("/planes/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        api.perform(delete("/planes/1")).andExpect(status().isNoContent());
        api.perform(delete("/planes/2")).andExpect(status().isNotFound());
        api.perform(delete("/planes/3")).andExpect(status().isConflict());
        verify(planes, never()).delete(any(Plan.class));
    }

    @Test
    void actualizarUsuarioConservaElHashExistente() {
        var usuarios = mock(UsuarioSistemaRepository.class);
        var roles = mock(RolRepository.class);
        var usuario = UsuarioSistema.builder().idUsuario(1L).claveHash("hash existente").build();
        var rol = Rol.builder().idRol(2L).nombre("CLIENTE").build();
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(roles.findById(2L)).thenReturn(Optional.of(rol));

        new UsuarioSistemaService(usuarios, roles).actualizar(1L,
                new UsuarioSistemaUpdateDTO("CLIENTE@ejemplo.com", "activo", 2L));

        assertEquals("cliente@ejemplo.com", usuario.getCorreo());
        assertSame(rol, usuario.getRol());
        assertEquals("hash existente", usuario.getClaveHash());
    }

    @Test
    void unaZonaConBoletasConservaSuFuncionYElAforoMinimo() {
        var zonas = mock(ZonaRepository.class);
        var funciones = mock(FuncionRepository.class);
        var funcion = Funcion.builder().idFuncion(2L).build();
        var zona = Zona.builder().idZona(1L).aforo(10).funcion(funcion).build();
        when(zonas.findById(1L)).thenReturn(Optional.of(zona));
        when(funciones.findById(2L)).thenReturn(Optional.of(funcion));
        when(funciones.findById(3L)).thenReturn(Optional.of(Funcion.builder().idFuncion(3L).build()));
        when(boletas.existsByZonaIdZona(1L)).thenReturn(true);
        when(boletas.countByZonaIdZonaAndEstadoNot(1L, "ANULADA")).thenReturn(8L);
        var servicio = new ZonaService(boletas, zonas, funciones, bloqueo);

        assertThrows(BusinessRuleException.class,
                () -> servicio.actualizar(1L, new ZonaCreateDTO("General", 7, BigDecimal.TEN, 2L)));
        assertThrows(BusinessRuleException.class,
                () -> servicio.actualizar(1L, new ZonaCreateDTO("General", 10, BigDecimal.TEN, 3L)));
        assertEquals(8, servicio.actualizar(1L, new ZonaCreateDTO("General", 8, BigDecimal.TEN, 2L)).aforo());
        assertSame(funcion, zona.getFuncion());
    }

    @Test
    void reprogramarFuncionIgnoraSuPropioHorarioYProtegeLasBoletas() {
        var funciones = mock(FuncionRepository.class);
        var atracciones = mock(AtraccionRepository.class);
        var atraccion = Atraccion.builder().idAtraccion(2L).build();
        var fecha = BoletaService.ahora().toLocalDate().plusDays(5);
        var funcion = Funcion.builder().idFuncion(1L).fecha(fecha).atraccion(atraccion)
                .horaApertura(LocalTime.of(10, 0)).horaCierre(LocalTime.NOON).estado("PROGRAMADA").build();
        var otra = Funcion.builder().idFuncion(2L).fecha(fecha).atraccion(atraccion)
                .horaApertura(LocalTime.of(13, 0)).horaCierre(LocalTime.of(15, 0)).estado("PROGRAMADA").build();
        when(funciones.findById(1L)).thenReturn(Optional.of(funcion));
        when(funciones.findByAtraccionIdAtraccion(2L)).thenReturn(List.of(funcion, otra));
        when(atracciones.findById(2L)).thenReturn(Optional.of(atraccion));
        var servicio = new FuncionService(funciones, atracciones, boletas, bloqueo, mock(NotificacionService.class));

        var horario = new FuncionUpdateDTO(fecha, LocalTime.of(11, 0), LocalTime.of(13, 0), 2L);
        assertEquals(LocalTime.of(11, 0), servicio.actualizar(1L, horario).horaApertura());
        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L,
                new FuncionUpdateDTO(fecha, LocalTime.of(12, 0), LocalTime.of(14, 0), 2L)));
        when(boletas.existsByZonaFuncionIdFuncion(1L)).thenReturn(true);
        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L, horario));
    }

    @Test
    void reservaSoloPermiteEditarElPlanMientrasEsteVigenteYEliminarSiEstaVacia() {
        var reservas = mock(ReservaRepository.class);
        var planes = mock(PlanRepository.class);
        var reserva = Reserva.builder().idReserva(1L).fechaHora(BoletaService.ahora()).estado("PENDIENTE").build();
        var plan = Plan.builder().idPlan(2L).build();
        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));
        when(planes.findById(2L)).thenReturn(Optional.of(plan));
        when(reservas.eliminarSinCascada(1L)).thenReturn(1);
        var servicio = new ReservaService(reservas, mock(PerfilClienteRepository.class), planes,
                boletas, mock(BoletaService.class), bloqueo, mock(NotificacionService.class));

        servicio.actualizar(1L, new ReservaUpdateDTO(2L));
        assertSame(plan, reserva.getPlan());
        when(boletas.existsByReservaIdReserva(1L)).thenReturn(true);
        assertThrows(BusinessRuleException.class, () -> servicio.eliminar(1L));
        reserva.setEstado("CONFIRMADA");
        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L, new ReservaUpdateDTO(null)));
        reserva.setEstado("PENDIENTE");
        when(boletas.existsByReservaIdReserva(1L)).thenReturn(false);
        assertDoesNotThrow(() -> servicio.eliminar(1L));
    }

    @Test
    void boletaConservaSusDatosYLaEliminacionNoSustituyeLaAnulacion() {
        var servicio = new BoletaService(boletas, mock(ReservaRepository.class), mock(ZonaRepository.class),
                mock(TipoEntradaRepository.class), mock(ComboRepository.class), mock(ComboAtraccionRepository.class), bloqueo);
        var reserva = Reserva.builder().estado("CONFIRMADA").build();
        var boleta = Boleta.builder().estado("ACTIVA").reserva(reserva).build();
        when(boletas.findById(1L)).thenReturn(Optional.of(boleta));
        when(boletas.eliminarSinCascada(1L)).thenReturn(1);

        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L));
        assertThrows(BusinessRuleException.class, () -> servicio.eliminar(1L));
        boleta.setEstado("ANULADA");
        boleta.setCombo(Combo.builder().idCombo(2L).build());
        assertThrows(BusinessRuleException.class, () -> servicio.eliminar(1L));
        boleta.setCombo(null);
        assertDoesNotThrow(() -> servicio.eliminar(1L));
        verify(boletas).eliminarSinCascada(1L);
    }

    @Test
    void comboAtraccionUsaClaveCompuestaYRechazaAlterarElTotal() {
        var enlaces = mock(ComboAtraccionRepository.class);
        var combos = mock(ComboRepository.class);
        var atracciones = mock(AtraccionRepository.class);
        var administracion = new ComboService(combos, atracciones, enlaces, boletas, bloqueo);
        var servicio = new ComboAtraccionService(enlaces, combos, administracion, bloqueo);
        var combo = Combo.builder().idCombo(2L).build();
        var id = new ComboAtraccionId(2L, 3L);
        var enlace = ComboAtraccion.builder().id(id).combo(combo).porcentajePrecio(new BigDecimal("100.00")).build();
        when(combos.findById(2L)).thenReturn(Optional.of(combo));
        when(enlaces.findById(id)).thenReturn(Optional.of(enlace));
        when(enlaces.findByIdIdCombo(2L)).thenReturn(List.of(enlace));

        assertEquals(3L, servicio.obtenerPorId(2L, 3L).idAtraccion());
        assertThrows(BusinessRuleException.class, () -> servicio.crear(2L, 4L, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(2L, 3L, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> servicio.eliminar(2L, 3L));
        assertThrows(BusinessRuleException.class, () -> administracion.eliminar(2L));
        verify(combos, never()).eliminarSinCascada(2L);
        verify(enlaces, never()).eliminarPorCombo(2L);
        verify(enlaces, never()).delete(any(ComboAtraccion.class));
    }
}
