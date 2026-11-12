package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRESTADORES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRESTADOR")
    private Long idPrestador;

    @Column(name = "NIT", nullable = false, unique = true, length = 30)
    private String nit;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "CORREO", nullable = false, length = 150)
    private String correo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MUNICIPIO", nullable = false)
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_PRESTADOR", nullable = false)
    private TipoPrestador tipoPrestador;

    @OneToMany(mappedBy = "prestador", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Plan> planes = new ArrayList<>();

    @OneToMany(mappedBy = "prestador", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Atraccion> atracciones = new ArrayList<>();
}
