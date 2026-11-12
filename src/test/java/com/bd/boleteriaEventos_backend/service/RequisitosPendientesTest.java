package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.controller.UsuarioSistemaController;
import com.bd.boleteriaEventos_backend.dto.*;
import com.bd.boleteriaEventos_backend.entity.*;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RequisitosPendientesTest {

    @Test
    void registraConBCryptYAutenticaSoloUsuariosActivosConHashValido() {
        var usuarios = mock(UsuarioSistemaRepository.class);
        var roles = mock(RolRepository.class);
        var servicio = new UsuarioSistemaService(usuarios, roles);
        var rol = Rol.builder().idRol(1L).nombre("CLIENTE").build();
        when(roles.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarios.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var alta = new UsuarioSistemaCreateDTO("cliente@ejemplo.com", "Clave segura", "ACTIVO", 1L);

        servicio.crear(alta);

        var captor = ArgumentCaptor.forClass(UsuarioSistema.class);
        verify(usuarios).save(captor.capture());
        var usuario = captor.getValue();
        assertNotEquals(alta.clave(), usuario.getClaveHash());
        assertTrue(new BCryptPasswordEncoder().matches(alta.clave(), usuario.getClaveHash()));

        when(usuarios.findByCorreoIgnoreCase(alta.correo())).thenReturn(Optional.of(usuario));
        var credenciales = new LoginRequestDTO(alta.correo(), alta.clave());
        assertEquals("CLIENTE", servicio.autenticar(credenciales).orElseThrow().nombreRol());
        assertTrue(servicio.autenticar(new LoginRequestDTO(alta.correo(), "incorrecta")).isEmpty());
        usuario.setEstado("INACTIVO");
        assertTrue(servicio.autenticar(credenciales).isEmpty());
        usuario.setEstado("ACTIVO");
        usuario.setClaveHash(alta.clave());
        assertTrue(servicio.autenticar(credenciales).isEmpty());
    }

    @Test
    void elRegistroSigueAdmitiendoClavesLargasSinTruncarlas() {
        var usuarios = mock(UsuarioSistemaRepository.class);
        var roles = mock(RolRepository.class);
        var servicio = new UsuarioSistemaService(usuarios, roles);
        when(roles.findById(1L)).thenReturn(Optional.of(Rol.builder().idRol(1L).build()));
        when(usuarios.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String clave = "ñ".repeat(40);
        String correo = "cliente@ejemplo.com";

        servicio.crear(new UsuarioSistemaCreateDTO(correo, clave, "ACTIVO", 1L));
        var captor = ArgumentCaptor.forClass(UsuarioSistema.class);
        verify(usuarios).save(captor.capture());
        when(usuarios.findByCorreoIgnoreCase(correo)).thenReturn(Optional.of(captor.getValue()));

        assertTrue(servicio.autenticar(new LoginRequestDTO(correo, clave)).isPresent());
        assertTrue(servicio.autenticar(new LoginRequestDTO(correo, clave + "x")).isEmpty());
    }

    @Test
    void loginResponde401YConservaLasRutasDeRegistroYConsulta() throws Exception {
        var servicio = mock(UsuarioSistemaService.class);
        var api = MockMvcBuilders.standaloneSetup(new UsuarioSistemaController(servicio)).build();
        var usuario = new UsuarioSistemaResponseDTO(1L, "cliente@ejemplo.com", "ACTIVO", 2L, "CLIENTE");
        when(servicio.autenticar(new LoginRequestDTO(usuario.correo(), "correcta"))).thenReturn(Optional.of(usuario));
        when(servicio.obtenerPorId(1L)).thenReturn(usuario);
        when(servicio.crear(any())).thenReturn(usuario);

        api.perform(post("/usuarios-sistema/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"cliente@ejemplo.com\",\"clave\":\"correcta\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombreRol").value("CLIENTE"))
                .andExpect(jsonPath("$.claveHash").doesNotExist());
        api.perform(post("/usuarios-sistema/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"cliente@ejemplo.com\",\"clave\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
        api.perform(get("/usuarios-sistema/1")).andExpect(status().isOk());
        api.perform(post("/usuarios-sistema").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"cliente@ejemplo.com\",\"clave\":\"correcta\",\"estado\":\"ACTIVO\",\"idRol\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void traduceLaRestriccionReferencialAlEliminarUnMunicipio() {
        var municipios = mock(MunicipioRepository.class);
        when(municipios.eliminarSinCascada(1L)).thenThrow(new DataIntegrityViolationException("FK"));
        assertThrows(BusinessRuleException.class, () -> new MunicipioService(municipios).eliminar(1L));
    }

    @Test
    void actualizarPerfilValidaDocumentoYConservaElUsuario() {
        var perfiles = mock(PerfilClienteRepository.class);
        var servicio = new PerfilClienteService(perfiles, mock(UsuarioSistemaRepository.class));
        var usuario = UsuarioSistema.builder().idUsuario(9L).build();
        var perfil = PerfilCliente.builder().idPerfil(1L).usuario(usuario).build();
        when(perfiles.findById(1L)).thenReturn(Optional.of(perfil));
        var datos = new PerfilClienteUpdateDTO("123", "Nombre actualizado", null);
        when(perfiles.findByDocumento("123")).thenReturn(Optional.of(PerfilCliente.builder().idPerfil(2L).build()));
        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L, datos));

        when(perfiles.findByDocumento("123")).thenReturn(Optional.empty());
        servicio.actualizar(1L, datos);
        assertEquals("123", perfil.getDocumento());
        assertEquals("Nombre actualizado", perfil.getNombre());
        assertSame(usuario, perfil.getUsuario());
    }

    @Test
    void actualizarTipoEntradaConservaLaClasificacionSiNoSeEnvia() {
        var tipos = mock(TipoEntradaRepository.class);
        var tipo = TipoEntrada.builder().idTipoEntrada(1L).esAdulto(true).build();
        when(tipos.findById(1L)).thenReturn(Optional.of(tipo));

        new TipoEntradaService(tipos).actualizar(1L,
                new TipoEntradaCreateDTO("Adulto", new BigDecimal("20.00"), false, null));

        assertTrue(tipo.admiteAcompanamiento());
        assertEquals(new BigDecimal("20.00"), tipo.getPorcentajeDescuento());
    }

    @Test
    void disponibilidadReutilizaElConteoQueExcluyeAnuladas() {
        var zonas = mock(ZonaRepository.class);
        var funciones = mock(FuncionRepository.class);
        var boletas = mock(BoletaRepository.class);
        when(funciones.existsById(1L)).thenReturn(true);
        when(zonas.findByFuncionIdFuncion(1L)).thenReturn(
                List.of(Zona.builder().idZona(2L).nombre("General").aforo(10).build()));
        when(boletas.countByZonaIdZonaAndEstadoNot(2L, "ANULADA")).thenReturn(3L);

        var bloqueo = mock(BloqueoBoleteria.class);
        var zona = new ZonaService(boletas, zonas, funciones, bloqueo).disponibilidadPorFuncion(1L).zonas().getFirst();
        verifyNoInteractions(bloqueo);

        assertEquals(new DisponibilidadZonaDTO(2L, "General", 10, 3L, 7L), zona);
        verify(boletas).countByZonaIdZonaAndEstadoNot(2L, "ANULADA");
        verifyNoMoreInteractions(boletas);
    }

    @Test
    void protegeCombosCompradosYValidaLaSumaDelReparto() {
        var combos = mock(ComboRepository.class);
        var composicion = mock(ComboAtraccionRepository.class);
        var boletas = mock(BoletaRepository.class);
        var servicio = new ComboService(combos, mock(AtraccionRepository.class), composicion,
                boletas, mock(BloqueoBoleteria.class));
        when(combos.findById(1L)).thenReturn(Optional.of(Combo.builder().idCombo(1L).build()));
        when(boletas.existsByComboIdCombo(1L)).thenReturn(true);
        var repartoIncompleto = new ComboUpdateDTO("Combo", BigDecimal.TEN, 5, "ACTIVO",
                List.of(new ComboUpdateDTO.AtraccionReparto(2L, BigDecimal.TEN)));
        var repartoCompleto = new ComboUpdateDTO("Combo", BigDecimal.TEN, 5, "ACTIVO",
                List.of(new ComboUpdateDTO.AtraccionReparto(2L, new BigDecimal("100"))));

        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L, repartoIncompleto));
        assertThrows(BusinessRuleException.class, () -> servicio.actualizar(1L, repartoCompleto));
        assertThrows(BusinessRuleException.class, () -> servicio.eliminar(1L));
        verify(composicion, never()).eliminarPorCombo(1L);
        verify(combos, never()).eliminarSinCascada(1L);
    }

    @Test
    void reportesDeIngresosAplicanLosMismosEstadosDeVenta() {
        var funciones = mock(FuncionRepository.class);
        var boletas = mock(BoletaRepository.class);
        var servicio = new ReporteService(funciones, boletas);

        servicio.aforoPorFuncion();
        servicio.ingresosPorAtraccionYTipo();
        servicio.combosMasVendidos();

        verify(funciones).reportarAforo();
        verify(boletas).reportarIngresos("CONFIRMADA", List.of("ACTIVA", "USADA"));
        verify(boletas).reportarCombos("CONFIRMADA", List.of("ACTIVA", "USADA"));
    }

    @Test
    void confirmarDosVecesNotificaSoloLaTransicion() {
        var reservas = mock(ReservaRepository.class);
        var boletas = mock(BoletaRepository.class);
        var notificaciones = mock(NotificacionService.class);
        var servicio = new ReservaService(reservas, mock(PerfilClienteRepository.class),
                mock(PlanRepository.class), boletas, mock(BoletaService.class),
                mock(BloqueoBoleteria.class), notificaciones);
        var reserva = Reserva.builder().idReserva(1L).fechaHora(BoletaService.ahora()).estado("PENDIENTE").build();
        var funcion = Funcion.builder().fecha(BoletaService.ahora().toLocalDate().plusDays(1))
                .horaCierre(java.time.LocalTime.NOON).estado("PROGRAMADA").build();
        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));
        when(boletas.findByReservaIdReserva(1L)).thenReturn(List.of(Boleta.builder()
                .estado("ACTIVA").zona(Zona.builder().funcion(funcion).build()).build()));

        servicio.confirmar(1L);
        servicio.confirmar(1L);

        verify(notificaciones).notificarReserva(eq(reserva), eq("Reserva confirmada"), anyString());
    }
}
