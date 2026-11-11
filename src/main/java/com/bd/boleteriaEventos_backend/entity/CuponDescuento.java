package com.bd.boleteriaEventos_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUPONES_DESCUENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUPON")
    private Long idCupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EVENTO")
    private Evento evento;

    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "PORCENTAJE_DESCUENTO")
    private BigDecimal porcentajeDescuento;

    @Column(name = "USOS_MAXIMOS")
    private Integer usosMaximos;

    @Column(name = "USOS_ACTUALES")
    private Integer usosActuales;

    @Column(name = "FECHA_EXPIRACION")
    private LocalDateTime fechaExpiracion;

    @Column(name = "ACTIVO", columnDefinition = "NUMBER(1)")
    private Boolean activo;
}
