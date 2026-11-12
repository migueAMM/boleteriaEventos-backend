package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FUNCIONES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FUNCION")
    private Long idFuncion;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Convert(converter = HoraFuncionConverter.class)
    @ColumnTransformer(
            read = "(EXTRACT(HOUR FROM HORA_APERTURA) * 3600 + EXTRACT(MINUTE FROM HORA_APERTURA) * 60 + EXTRACT(SECOND FROM HORA_APERTURA))",
            write = "NUMTODSINTERVAL(?, 'SECOND')")
    @Column(name = "HORA_APERTURA", nullable = false, columnDefinition = "INTERVAL DAY(0) TO SECOND(6)")
    private LocalTime horaApertura;

    @Convert(converter = HoraFuncionConverter.class)
    @ColumnTransformer(
            read = "(EXTRACT(HOUR FROM HORA_CIERRE) * 3600 + EXTRACT(MINUTE FROM HORA_CIERRE) * 60 + EXTRACT(SECOND FROM HORA_CIERRE))",
            write = "NUMTODSINTERVAL(?, 'SECOND')")
    @Column(name = "HORA_CIERRE", nullable = false, columnDefinition = "INTERVAL DAY(0) TO SECOND(6)")
    private LocalTime horaCierre;

    @Column(name = "ESTADO", nullable = false, length = 30)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ATRACCION", nullable = false)
    private Atraccion atraccion;

    @OneToMany(mappedBy = "funcion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Zona> zonas = new ArrayList<>();
}
