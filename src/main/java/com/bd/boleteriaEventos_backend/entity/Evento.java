package com.bd.boleteriaEventos_backend.entity;

import com.bd.boleteriaEventos_backend.enums.EstadoEvento;
import com.bd.boleteriaEventos_backend.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "EVENTOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Long idEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORGANIZADOR")
    private Organizador organizador;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "SLUG")
    private String slug;

    @Lob
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO")
    private TipoEvento tipoEvento;

    @Column(name = "CIUDAD")
    private String ciudad;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "LUGAR_NOMBRE")
    private String lugarNombre;

    @Column(name = "FECHA_INICIO_EVENTO")
    private LocalDateTime fechaInicioEvento;

    @Column(name = "FECHA_FIN_EVENTO")
    private LocalDateTime fechaFinEvento;

    @Column(name = "FECHA_APERTURA_INSCRIPCIONES")
    private LocalDateTime fechaAperturaInscripciones;

    @Column(name = "FECHA_CIERRE_INSCRIPCIONES")
    private LocalDateTime fechaCierreInscripciones;

    @Column(name = "IMAGEN_BANNER_URL")
    private String imagenBannerUrl;

    @Column(name = "AFORO_MAXIMO_TOTAL")
    private Integer aforoMaximoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoEvento estado;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<CategoriaEvento> categorias;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<CuponDescuento> cupones;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<OrdenCompra> ordenes;
}
