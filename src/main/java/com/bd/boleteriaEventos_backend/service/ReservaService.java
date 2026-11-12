package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.ReservaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.ReservaResponseDTO;
import com.bd.boleteriaEventos_backend.entity.Boleta;
import com.bd.boleteriaEventos_backend.entity.PerfilCliente;
import com.bd.boleteriaEventos_backend.entity.Plan;
import com.bd.boleteriaEventos_backend.entity.Reserva;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.BoletaRepository;
import com.bd.boleteriaEventos_backend.repository.PerfilClienteRepository;
import com.bd.boleteriaEventos_backend.repository.PlanRepository;
import com.bd.boleteriaEventos_backend.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PerfilClienteRepository perfilClienteRepository;
    private final PlanRepository planRepository;
    private final BoletaRepository boletaRepository;
    private final BoletaService boletaService;
    private final BloqueoBoleteria bloqueo;

    @Transactional
    public ReservaResponseDTO crear(ReservaCreateDTO dto) {
        bloqueo.bloquearEscrituras();
        if (dto.idCombo() != null && (dto.boletas() == null || dto.boletas().isEmpty())) {
            throw new BusinessRuleException("Seleccione las funciones del combo");
        }
        PerfilCliente perfil = perfilClienteRepository.findById(dto.idPerfil())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado con ID: " + dto.idPerfil()));

        Plan plan = null;
        if (dto.idPlan() != null) {
            plan = planRepository.findById(dto.idPlan())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + dto.idPlan()));
        }

        Reserva reserva = Reserva.builder()
                .fechaHora(BoletaService.ahora())
                .estado("PENDIENTE")
                .perfilCliente(perfil)
                .plan(plan)
                .build();

        Reserva guardada = reservaRepository.save(reserva);
        if (dto.boletas() != null && !dto.boletas().isEmpty()) {
            boletaService.emitir(guardada, dto.boletas(), dto.idCombo());
        }
        return mapToDTO(guardada);
    }

    @Transactional
    public ReservaResponseDTO confirmar(Long id) {
        bloqueo.bloquearEscrituras();
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
        if ("CONFIRMADA".equals(reserva.getEstado())) {
            return mapToDTO(reserva);
        }
        LocalDateTime ahora = BoletaService.ahora();
        if (!"PENDIENTE".equals(reserva.getEstado()) || !ahora.isBefore(
                reserva.getFechaHora().plusMinutes(BoletaService.MINUTOS_RESERVA))) {
            throw new BusinessRuleException("La reserva debe estar pendiente y vigente");
        }
        List<Boleta> boletas = boletaRepository.findByReservaIdReserva(id);
        if (boletas.isEmpty() || boletas.stream().anyMatch(boleta -> !"ACTIVA".equals(boleta.getEstado()) || boleta.getZona() == null)) {
            throw new BusinessRuleException("La reserva debe contener boletas activas");
        }
        for (Boleta boleta : boletas) {
            BoletaService.validarFuncionEnVenta(boleta.getZona().getFuncion(), ahora);
        }
        reserva.setEstado("CONFIRMADA");
        return mapToDTO(reserva);
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    @Transactional
    public void expirarPendientes() {
        LocalDateTime fechaLimite = BoletaService.ahora().minusMinutes(BoletaService.MINUTOS_RESERVA);
        if (!reservaRepository.existsByEstadoAndFechaHoraLessThanEqual("PENDIENTE", fechaLimite)) {
            return;
        }
        bloqueo.bloquearEscrituras();
        for (Reserva reserva : reservaRepository.findByEstadoAndFechaHoraLessThanEqual("PENDIENTE", fechaLimite)) {
            reserva.setEstado("EXPIRADA");
            boletaRepository.findByReservaIdReserva(reserva.getIdReserva()).forEach(BoletaService::anularVigente);
        }
    }

    public List<ReservaResponseDTO> listarTodos() {
        return reservaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ReservaResponseDTO obtenerPorId(Long id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
        return mapToDTO(r);
    }

    public List<ReservaResponseDTO> listarPorPerfil(Long idPerfil) {
        return reservaRepository.findByPerfilClienteIdPerfil(idPerfil).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ReservaResponseDTO mapToDTO(Reserva r) {
        return new ReservaResponseDTO(
                r.getIdReserva(),
                r.getFechaHora(),
                r.getEstado(),
                r.getPerfilCliente() != null ? r.getPerfilCliente().getIdPerfil() : null,
                r.getPerfilCliente() != null ? r.getPerfilCliente().getNombre() : null,
                r.getPlan() != null ? r.getPlan().getIdPlan() : null,
                r.getPlan() != null ? r.getPlan().getNombre() : null
        );
    }
}
