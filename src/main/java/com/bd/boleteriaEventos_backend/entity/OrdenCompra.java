package com.bd.boleteriaEventos_backend.entity;

import com.bd.boleteriaEventos_backend.enums.EstadoOrden;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORDENES_COMPRA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDEN")
    private Long idOrden;

    @Column(name = "CODIGO_ORDEN")
    private String codigoOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPRADOR")
    private Usuario comprador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EVENTO")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CUPON")
    private CuponDescuento cupon;

    @Column(name = "SUBTOTAL")
    private BigDecimal subtotal;

    @Column(name = "TOTAL_CARGOS_SERVICIO")
    private BigDecimal totalCargosServicio;

    @Column(name = "TOTAL_DESCUENTO")
    private BigDecimal totalDescuento;

    @Column(name = "MONTO_TOTAL")
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ORDEN")
    private EstadoOrden estadoOrden;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_EXPIRACION_RESERVA")
    private LocalDateTime fechaExpiracionReserva;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<TicketInscripcion> tickets;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<PagoTransaccion> pagos;
}
