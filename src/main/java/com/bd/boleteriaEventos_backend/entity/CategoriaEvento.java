package com.bd.boleteriaEventos_backend.entity;

import com.bd.boleteriaEventos_backend.enums.EstadoCategoria;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CATEGORIAS_EVENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORIA")
    private Long idCategoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EVENTO")
    private Evento evento;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "PRECIO_BASE")
    private BigDecimal precioBase;

    @Column(name = "CARGO_SERVICIO")
    private BigDecimal cargoServicio;

    @Column(name = "CUPO_MAXIMO")
    private Integer cupoMaximo;

    @Column(name = "CUPOS_DISPONIBLES")
    private Integer cuposDisponibles;

    @Column(name = "EDAD_MINIMA")
    private Integer edadMinima;

    @Column(name = "EDAD_MAXIMA")
    private Integer edadMaxima;

    @Column(name = "INCLUYE_KIT", columnDefinition = "NUMBER(1)")
    private Boolean incluyeKit;

    @Column(name = "FECHA_INICIO_VENTA")
    private LocalDateTime fechaInicioVenta;

    @Column(name = "FECHA_FIN_VENTA")
    private LocalDateTime fechaFinVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoCategoria estado;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<TicketInscripcion> tickets;
}
