package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MUNICIPIOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MUNICIPIO")
    private Long idMunicipio;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "DEPARTAMENTO", nullable = false, length = 100)
    private String departamento;

    @OneToMany(mappedBy = "municipio", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Prestador> prestadores = new ArrayList<>();
}
