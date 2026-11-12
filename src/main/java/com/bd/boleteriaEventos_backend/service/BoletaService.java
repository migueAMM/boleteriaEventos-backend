package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.BoletaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.BoletaResponseDTO;
import com.bd.boleteriaEventos_backend.dto.SeleccionBoletaDTO;
import com.bd.boleteriaEventos_backend.entity.*;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoletaService {

    static final int MINUTOS_RESERVA = 15;
    private static final int LIMITE_POR_FUNCION = 6;
    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");

    private static final String ACTIVA = "ACTIVA";
    private static final String USADA = "USADA";
    private static final String ANULADA = "ANULADA";

    private final BoletaRepository boletaRepository;
    private final ReservaRepository reservaRepository;
    private final ZonaRepository zonaRepository;
    private final TipoEntradaRepository tipoEntradaRepository;
    private final ComboRepository comboRepository;
    private final ComboAtraccionRepository comboAtraccionRepository;
    private final BloqueoBoleteria bloqueo;

    static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_HORARIA);
    }

    @Transactional
    public BoletaResponseDTO crear(BoletaCreateDTO dto) {
        bloqueo.bloquearEscrituras();
        if (dto.idCombo() != null) {
            throw new BusinessRuleException("Compre el combo con todas sus boletas al crear la reserva");
        }
        Reserva reserva = reservaRepository.findById(dto.idReserva())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + dto.idReserva()));
        return mapToDTO(emitir(reserva, List.of(new SeleccionBoletaDTO(dto.idZona(), dto.idTipoEntrada())), null).getFirst());
    }

    // El flujo de reserva y la emisión individual adquieren el bloqueo antes de leer o escribir.
    List<Boleta> emitir(Reserva reserva, List<SeleccionBoletaDTO> selecciones, Long idCombo) {
        LocalDateTime ahora = ahora();
        if (!"PENDIENTE".equals(reserva.getEstado())
                || !ahora.isBefore(reserva.getFechaHora().plusMinutes(MINUTOS_RESERVA))) {
            throw new BusinessRuleException("La reserva debe estar pendiente y vigente");
        }

        List<Boleta> existentes = boletaRepository.findByReservaIdReserva(reserva.getIdReserva());
        if (existentes.stream().anyMatch(boleta -> boleta.getCombo() != null)) {
            throw new BusinessRuleException("Una reserva de combo ya contiene todas sus boletas");
        }

        Combo combo = idCombo == null ? null : comboRepository.findById(idCombo)
                .orElseThrow(() -> new ResourceNotFoundException("Combo no encontrado con ID: " + idCombo));
        Map<Long, BigDecimal> reparto = new HashMap<>();
        if (combo != null) {
            if (!"ACTIVO".equals(combo.getEstado()) || !existentes.isEmpty()) {
                throw new BusinessRuleException("El combo debe estar activo y ocupar su propia reserva");
            }
            for (ComboAtraccion atraccion : comboAtraccionRepository.findByIdIdCombo(idCombo)) {
                reparto.put(atraccion.getAtraccion().getIdAtraccion(), atraccion.getPorcentajePrecio());
            }
            BigDecimal total = reparto.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            if (total.compareTo(BigDecimal.valueOf(100)) != 0 || reparto.size() != selecciones.size()) {
                throw new BusinessRuleException("Seleccione una función por cada atracción de un combo con reparto de 100 %");
            }
        }

        Map<Long, Integer> nuevasPorZona = new HashMap<>();
        Map<Long, Integer> nuevasPorFuncion = new HashMap<>();
        List<Boleta> nuevas = new ArrayList<>();
        for (SeleccionBoletaDTO seleccion : selecciones) {
            Zona zona = zonaRepository.findById(seleccion.idZona())
                    .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada con ID: " + seleccion.idZona()));
            TipoEntrada tipo = tipoEntradaRepository.findById(seleccion.idTipoEntrada())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de entrada no encontrado con ID: " + seleccion.idTipoEntrada()));
            Funcion funcion = zona.getFuncion();
            LocalDateTime apertura = funcion.getFecha().atTime(funcion.getHoraApertura());
            LocalDateTime cierre = funcion.getFecha().atTime(funcion.getHoraCierre());
            validarFuncionEnVenta(funcion, ahora);
            BigDecimal precioBase = zona.getPrecio();
            if (combo != null) {
                BigDecimal porcentaje = reparto.remove(funcion.getAtraccion().getIdAtraccion());
                if (porcentaje == null || apertura.isBefore(ahora)
                        || cierre.isAfter(ahora.plusDays(combo.getDiasVigencia()))) {
                    throw new BusinessRuleException("Cada función debe corresponder a una atracción distinta del combo y quedar dentro de su vigencia");
                }
                precioBase = combo.getPrecio().multiply(porcentaje).movePointLeft(2);
            }

            long ocupadas = boletaRepository.countByZonaIdZonaAndEstadoNot(zona.getIdZona(), ANULADA);
            if (ocupadas + nuevasPorZona.merge(zona.getIdZona(), 1, Integer::sum) > zona.getAforo()) {
                throw new BusinessRuleException("No hay cupo suficiente en la zona " + zona.getIdZona());
            }
            Long idFuncion = funcion.getIdFuncion();
            long compradas = boletaRepository.countByReservaPerfilClienteIdPerfilAndZonaFuncionIdFuncionAndEstadoNot(
                    reserva.getPerfilCliente().getIdPerfil(), idFuncion, ANULADA);
            if (compradas + nuevasPorFuncion.merge(idFuncion, 1, Integer::sum) > LIMITE_POR_FUNCION) {
                throw new BusinessRuleException("El perfil supera el límite de 6 boletas para la función " + idFuncion);
            }

            BigDecimal precio = precioBase.multiply(BigDecimal.ONE.subtract(tipo.getPorcentajeDescuento().movePointLeft(2)))
                    .setScale(2, RoundingMode.HALF_UP);
            nuevas.add(Boleta.builder()
                    .codigo(UUID.randomUUID().toString())
                    .estado(ACTIVA)
                    .precioPagado(precio)
                    .reserva(reserva)
                    .zona(zona)
                    .tipoEntrada(tipo)
                    .combo(combo)
                    .build());
        }

        boolean requiereAdulto = nuevas.stream().anyMatch(boleta -> Boolean.TRUE.equals(boleta.getTipoEntrada().getRequiereAdulto()));
        if (requiereAdulto && nuevas.stream().noneMatch(this::esEntradaAdulto)
                && existentes.stream().noneMatch(this::esEntradaAdulto)) {
            throw new BusinessRuleException("La reserva requiere al menos una entrada de adulto");
        }

        return boletaRepository.saveAll(nuevas);
    }

    private boolean esEntradaAdulto(Boleta boleta) {
        TipoEntrada tipo = boleta.getTipoEntrada();
        return !ANULADA.equals(boleta.getEstado()) && tipo != null && tipo.admiteAcompanamiento();
    }

    static void validarFuncionEnVenta(Funcion funcion, LocalDateTime ahora) {
        if (funcionCancelada(funcion) || "CERRADA".equals(funcion.getEstado())
                || !ahora.isBefore(funcion.getFecha().atTime(funcion.getHoraCierre()))) {
            throw new BusinessRuleException("La función está cancelada o cerrada");
        }
    }

    static boolean funcionCancelada(Funcion funcion) {
        return "CANCELADA".equals(funcion.getEstado());
    }

    static void anularVigente(Boleta boleta) {
        if (!USADA.equals(boleta.getEstado()) && !ANULADA.equals(boleta.getEstado())) {
            boleta.setEstado(ANULADA);
        }
    }

    @Transactional
    public BoletaResponseDTO usar(String codigo) {
        bloqueo.bloquearEscrituras();
        Boleta boleta = boletaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada"));
        if (!ACTIVA.equals(boleta.getEstado()) || !"CONFIRMADA".equals(boleta.getReserva().getEstado())
                || boleta.getZona() == null) {
            throw new BusinessRuleException("La boleta debe estar activa, confirmada y asociada a una función");
        }
        Funcion funcion = boleta.getZona().getFuncion();
        LocalDateTime ahora = ahora();
        if (funcionCancelada(funcion) || "CERRADA".equals(funcion.getEstado())
                || ahora.isBefore(funcion.getFecha().atTime(funcion.getHoraApertura()))
                || !ahora.isBefore(funcion.getFecha().atTime(funcion.getHoraCierre()))) {
            throw new BusinessRuleException("La boleta solo se puede usar durante su función");
        }
        boleta.setEstado(USADA);
        return mapToDTO(boleta);
    }

    @Transactional
    public BoletaResponseDTO anular(Long id) {
        bloqueo.bloquearEscrituras();
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + id));
        if (USADA.equals(boleta.getEstado())) {
            throw new BusinessRuleException("Una boleta usada no puede anularse");
        }
        anularVigente(boleta);
        return mapToDTO(boleta);
    }

    public BoletaResponseDTO actualizar(Long id) {
        buscar(id);
        throw new BusinessRuleException("Los datos de la boleta son inmutables. Usa las operaciones de ingreso o anulación");
    }

    private Boleta buscar(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        bloqueo.bloquearEscrituras();
        Boleta boleta = buscar(id);
        String estadoReserva = boleta.getReserva().getEstado();
        if (!"ANULADA".equals(boleta.getEstado()) || boleta.getCombo() != null
                || !("CONFIRMADA".equals(estadoReserva) || "EXPIRADA".equals(estadoReserva))) {
            throw new BusinessRuleException("Solo se pueden eliminar boletas individuales anuladas de reservas finalizadas");
        }
        try {
            if (boletaRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Boleta no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
    }

    public List<BoletaResponseDTO> listarTodos() {
        return boletaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public BoletaResponseDTO obtenerPorId(Long id) {
        Boleta b = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con ID: " + id));
        return mapToDTO(b);
    }

    public List<BoletaResponseDTO> listarPorReserva(Long idReserva) {
        return boletaRepository.findByReservaIdReserva(idReserva).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public BoletaResponseDTO mapToDTO(Boleta b) {
        return new BoletaResponseDTO(
                b.getIdBoleta(),
                b.getCodigo(),
                b.getEstado(),
                b.getPrecioPagado(),
                b.getReserva() != null ? b.getReserva().getIdReserva() : null,
                b.getZona() != null ? b.getZona().getIdZona() : null,
                b.getZona() != null ? b.getZona().getNombre() : null,
                b.getTipoEntrada() != null ? b.getTipoEntrada().getIdTipoEntrada() : null,
                b.getTipoEntrada() != null ? b.getTipoEntrada().getNombre() : null,
                b.getCombo() != null ? b.getCombo().getIdCombo() : null,
                b.getCombo() != null ? b.getCombo().getNombre() : null
        );
    }
}
