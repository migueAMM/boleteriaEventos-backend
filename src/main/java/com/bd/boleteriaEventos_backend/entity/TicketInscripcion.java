package com.bd.boleteriaEventos_backend.entity;

import com.bd.boleteriaEventos_backend.enums.EstadoTicket;
import com.bd.boleteriaEventos_backend.enums.TallaCamiseta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TICKETS_INSCRIPCION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TICKET")
    private Long idTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDEN")
    private OrdenCompra orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEGORIA")
    private CategoriaEvento categoria;

    @Column(name = "CODIGO_TICKET_QR")
    private String codigoTicketQr;

    @Column(name = "TIPO_DOCUMENTO_ASISTENTE")
    private String tipoDocumentoAsistente;

    @Column(name = "DOCUMENTO_ASISTENTE")
    private String documentoAsistente;

    @Column(name = "NOMBRE_COMPLETO_ASISTENTE")
    private String nombreCompletoAsistente;

    @Column(name = "EMAIL_ASISTENTE")
    private String emailAsistente;

    @Column(name = "TELEFONO_ASISTENTE")
    private String telefonoAsistente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TALLA_CAMISETA")
    private TallaCamiseta tallaCamiseta;

    @Column(name = "CONTACTO_EMERGENCIA_NOMBRE")
    private String contactoEmergenciaNombre;

    @Column(name = "CONTACTO_EMERGENCIA_TELEFONO")
    private String contactoEmergenciaTelefono;

    @Column(name = "TIPO_SANGRE")
    private String tipoSangre;

    @Column(name = "NUMERO_DORSAL")
    private Integer numeroDorsal;

    @Column(name = "PRECIO_UNITARIO_PAGADO")
    private BigDecimal precioUnitarioPagado;

    @Column(name = "CHECK_IN_REALIZADO", columnDefinition = "NUMBER(1)")
    private Boolean checkInRealizado;

    @Column(name = "FECHA_CHECK_IN")
    private LocalDateTime fechaCheckIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_TICKET")
    private EstadoTicket estadoTicket;
}
