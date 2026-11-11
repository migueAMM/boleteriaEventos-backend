package com.bd.boleteriaEventos_backend.entity;

import com.bd.boleteriaEventos_backend.enums.EstadoPago;
import com.bd.boleteriaEventos_backend.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGOS_TRANSACCION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDEN")
    private OrdenCompra orden;

    @Column(name = "PASARELA_PAGO")
    private String pasarelaPago;

    @Column(name = "REFERENCIA_EXTERNA")
    private String referenciaExterna;

    @Enumerated(EnumType.STRING)
    @Column(name = "METODO_PAGO")
    private MetodoPago metodoPago;

    @Column(name = "MONTO")
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_PAGO")
    private EstadoPago estadoPago;

    @Column(name = "FECHA_PAGO")
    private LocalDateTime fechaPago;
}
