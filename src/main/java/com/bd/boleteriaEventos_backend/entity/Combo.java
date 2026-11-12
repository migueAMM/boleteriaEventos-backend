package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMBOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMBO")
    private Long idCombo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "PRECIO", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "DIAS_VIGENCIA", nullable = false)
    private Integer diasVigencia;

    @Column(name = "ESTADO", nullable = false, length = 30)
    private String estado;

    @OneToMany(mappedBy = "combo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<ComboAtraccion> comboAtracciones = new ArrayList<>();

    @OneToMany(mappedBy = "combo", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Boleta> boletas = new ArrayList<>();
}
