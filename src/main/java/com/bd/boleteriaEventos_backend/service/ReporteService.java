package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.AforoFuncionDTO;
import com.bd.boleteriaEventos_backend.dto.ComboVentasDTO;
import com.bd.boleteriaEventos_backend.dto.IngresosAtraccionDTO;
import com.bd.boleteriaEventos_backend.repository.BoletaRepository;
import com.bd.boleteriaEventos_backend.repository.FuncionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private static final List<String> ESTADOS_VENTA = List.of("ACTIVA", "USADA");
    private final FuncionRepository funcionRepository;
    private final BoletaRepository boletaRepository;

    public List<AforoFuncionDTO> aforoPorFuncion() {
        return funcionRepository.reportarAforo();
    }

    public List<IngresosAtraccionDTO> ingresosPorAtraccionYTipo() {
        return boletaRepository.reportarIngresos("CONFIRMADA", ESTADOS_VENTA);
    }

    public List<ComboVentasDTO> combosMasVendidos() {
        return boletaRepository.reportarCombos("CONFIRMADA", ESTADOS_VENTA);
    }
}
