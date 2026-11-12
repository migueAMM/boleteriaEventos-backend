package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TIPOS_PRESTADOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoPrestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO_PRESTADOR")
    private Long idTipoPrestador;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @OneToMany(mappedBy = "tipoPrestador", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Prestador> prestadores = new ArrayList<>();
}
