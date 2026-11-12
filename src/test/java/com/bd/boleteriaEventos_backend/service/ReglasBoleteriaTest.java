package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.ComboCreateDTO;
import com.bd.boleteriaEventos_backend.dto.FuncionCreateDTO;
import com.bd.boleteriaEventos_backend.dto.SeleccionBoletaDTO;
import com.bd.boleteriaEventos_backend.entity.*;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReglasBoleteriaTest {

    private final BloqueoBoleteria bloqueo = mock(BloqueoBoleteria.class);
    private final BoletaRepository boletas = mock(BoletaRepository.class);
    private final ReservaRepository reservas = mock(ReservaRepository.class);

    @Test
    void rechazaHorariosIgualesYSolapadosYAdmiteFuncionesContiguas() {
        FuncionRepository funciones = mock(FuncionRepository.class);
        AtraccionRepository atracciones = mock(AtraccionRepository.class);
        FuncionService servicio = new FuncionService(funciones, atracciones, boletas, bloqueo, mock(NotificacionService.class));
        var fecha = BoletaService.ahora().toLocalDate().plusDays(1);
        Atraccion atraccion = Atraccion.builder().idAtraccion(1L).build();
        when(atracciones.findById(1L)).thenReturn(Optional.of(atraccion));
        when(funciones.findByAtraccionIdAtraccion(1L)).thenReturn(List.of(Funcion.builder()
                .fecha(fecha).horaApertura(LocalTime.of(10, 0)).horaCierre(LocalTime.of(12, 0)).build()));
        when(funciones.save(any(Funcion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(BusinessRuleException.class, () -> servicio.crear(
                new FuncionCreateDTO(fecha, LocalTime.of(9, 0), LocalTime.of(9, 0), "PROGRAMADA", 1L)));
        assertThrows(BusinessRuleException.class, () -> servicio.crear(
                new FuncionCreateDTO(fecha, LocalTime.of(11, 0), LocalTime.of(13, 0), "PROGRAMADA", 1L)));
        assertDoesNotThrow(() -> servicio.crear(
                new FuncionCreateDTO(fecha, LocalTime.of(12, 0), LocalTime.of(13, 0), "PROGRAMADA", 1L)));
    }

    @Test
    void reparteExactamenteCienPorCientoEntreTresAtracciones() {
        ComboRepository combos = mock(ComboRepository.class);
        AtraccionRepository atracciones = mock(AtraccionRepository.class);
        ComboAtraccionRepository composicion = mock(ComboAtraccionRepository.class);
        ComboService servicio = new ComboService(combos, atracciones, composicion, boletas, bloqueo);
        when(combos.save(any(Combo.class))).thenAnswer(invocation -> {
            Combo combo = invocation.getArgument(0);
            combo.setIdCombo(1L);
            return combo;
        });
        for (long id = 1; id <= 3; id++) {
            when(atracciones.findById(id)).thenReturn(Optional.of(Atraccion.builder().idAtraccion(id).build()));
        }

        servicio.crear(new ComboCreateDTO("Combo", BigDecimal.TEN, 15, "ACTIVO", List.of(1L, 2L, 3L)));

        var captor = ArgumentCaptor.forClass(ComboAtraccion.class);
        verify(composicion, times(3)).save(captor.capture());
        assertEquals(new BigDecimal("100.00"), captor.getAllValues().stream()
                .map(ComboAtraccion::getPorcentajePrecio).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void rechazaUnaCompraQueSuperaElAforoOElLimitePorPerfil() {
        ZonaRepository zonas = mock(ZonaRepository.class);
        TipoEntradaRepository tipos = mock(TipoEntradaRepository.class);
        BoletaService servicio = new BoletaService(boletas, reservas, zonas, tipos,
                mock(ComboRepository.class), mock(ComboAtraccionRepository.class), bloqueo);
        Funcion funcion = Funcion.builder().idFuncion(2L).fecha(BoletaService.ahora().toLocalDate().plusDays(1))
                .horaApertura(LocalTime.of(10, 0)).horaCierre(LocalTime.of(12, 0)).estado("PROGRAMADA").build();
        Zona zona = Zona.builder().idZona(1L).aforo(8).precio(BigDecimal.TEN).funcion(funcion).build();
        TipoEntrada adulto = TipoEntrada.builder().nombre("General").esAdulto(true).requiereAdulto(false)
                .porcentajeDescuento(BigDecimal.ZERO).build();
        Reserva reserva = Reserva.builder().idReserva(3L).fechaHora(BoletaService.ahora()).estado("PENDIENTE")
                .perfilCliente(PerfilCliente.builder().idPerfil(4L).build()).build();
        when(zonas.findById(1L)).thenReturn(Optional.of(zona));
        when(tipos.findById(1L)).thenReturn(Optional.of(adulto));
        var seleccion = new SeleccionBoletaDTO(1L, 1L);
        when(boletas.countByZonaIdZonaAndEstadoNot(1L, "ANULADA")).thenReturn(7L);
        assertThrows(BusinessRuleException.class, () -> servicio.emitir(reserva, List.of(seleccion, seleccion), null));

        when(boletas.countByZonaIdZonaAndEstadoNot(1L, "ANULADA")).thenReturn(0L);
        when(boletas.countByReservaPerfilClienteIdPerfilAndZonaFuncionIdFuncionAndEstadoNot(4L, 2L, "ANULADA")).thenReturn(5L);
        assertThrows(BusinessRuleException.class, () -> servicio.emitir(reserva, List.of(seleccion, seleccion), null));
        verify(boletas, never()).saveAll(any());
    }

    @Test
    void expirarAnulaLasBoletasActivasDeLaReserva() {
        NotificacionService notificaciones = mock(NotificacionService.class);
        ReservaService servicio = new ReservaService(reservas, mock(PerfilClienteRepository.class),
                mock(PlanRepository.class), boletas, mock(BoletaService.class), bloqueo, notificaciones);
        Reserva pendiente = Reserva.builder().idReserva(3L).estado("PENDIENTE")
                .fechaHora(BoletaService.ahora().minusMinutes(16)).build();
        Boleta activa = Boleta.builder().estado("ACTIVA").build();
        when(reservas.existsByEstadoAndFechaHoraLessThanEqual(eq("PENDIENTE"), any())).thenReturn(true);
        when(reservas.findByEstadoAndFechaHoraLessThanEqual(eq("PENDIENTE"), any())).thenReturn(List.of(pendiente));
        when(boletas.findByReservaIdReserva(3L)).thenReturn(List.of(activa));

        servicio.expirarPendientes();

        verify(notificaciones).notificarReserva(eq(pendiente), eq("Reserva expirada"), anyString());
        assertEquals("EXPIRADA", pendiente.getEstado());
        assertEquals("ANULADA", activa.getEstado());
    }

    @Test
    void conservaLaPrecisionDeLaHoraAlConvertirlaAIntervalo() {
        var converter = new HoraFuncionConverter();
        var hora = LocalTime.of(23, 59, 59, 123_456_000);
        assertEquals(hora, converter.convertToEntityAttribute(converter.convertToDatabaseColumn(hora)));
    }

    @Test
    void cancelarAnulaLasActivasYConservaLosEstadosFinales() {
        FuncionRepository funciones = mock(FuncionRepository.class);
        NotificacionService notificaciones = mock(NotificacionService.class);
        FuncionService servicio = new FuncionService(funciones, mock(AtraccionRepository.class), boletas, bloqueo, notificaciones);
        Funcion funcion = Funcion.builder().idFuncion(1L).estado("PROGRAMADA").build();
        Reserva afectada = Reserva.builder().idReserva(2L).build();
        Boleta activa = Boleta.builder().estado("ACTIVA").reserva(afectada).build();
        Boleta usada = Boleta.builder().estado("USADA").build();
        Boleta anulada = Boleta.builder().estado("ANULADA").build();
        when(funciones.findById(1L)).thenReturn(Optional.of(funcion));
        when(boletas.findByZonaFuncionIdFuncion(1L)).thenReturn(List.of(activa, usada, anulada));

        servicio.cancelar(1L);
        servicio.cancelar(1L);

        verify(notificaciones).notificarReserva(eq(afectada), eq("Función cancelada"), anyString());

        assertEquals("CANCELADA", funcion.getEstado());
        assertEquals("ANULADA", activa.getEstado());
        assertEquals("USADA", usada.getEstado());
        assertEquals("ANULADA", anulada.getEstado());
    }
}
