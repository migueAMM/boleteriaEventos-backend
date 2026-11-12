package com.bd.boleteriaEventos_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "BOLETAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_BOLETA")
    private Long idBoleta;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "ESTADO", nullable = false, length = 30)
    private String estado;

    @Column(name = "PRECIO_PAGADO", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioPagado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RESERVA", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ZONA")
    private Zona zona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_ENTRADA")
    private TipoEntrada tipoEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMBO")
    private Combo combo;
}
