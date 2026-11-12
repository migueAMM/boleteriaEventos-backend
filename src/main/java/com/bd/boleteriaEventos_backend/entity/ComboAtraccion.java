package com.bd.boleteriaEventos_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "COMBOS_ATRACCIONES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboAtraccion {

    @EmbeddedId
    @Builder.Default
    private ComboAtraccionId id = new ComboAtraccionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCombo")
    @JoinColumn(name = "ID_COMBO", nullable = false)
    private Combo combo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idAtraccion")
    @JoinColumn(name = "ID_ATRACCION", nullable = false)
    private Atraccion atraccion;

    @Column(name = "PORCENTAJE_PRECIO", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajePrecio;
}
