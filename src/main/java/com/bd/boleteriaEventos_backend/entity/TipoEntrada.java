package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Table(name = "TIPOS_ENTRADA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEntrada {

    private static final Pattern NOMBRE_ADULTO = Pattern.compile("\\badulto\\b", Pattern.CASE_INSENSITIVE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO_ENTRADA")
    private Long idTipoEntrada;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "PORCENTAJE_DESCUENTO", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "REQUIERE_ADULTO", nullable = false)
    private Boolean requiereAdulto;

    @Column(name = "ES_ADULTO")
    private Boolean esAdulto;

    public boolean admiteAcompanamiento() {
        if (Boolean.TRUE.equals(requiereAdulto)) {
            return false;
        }
        if (esAdulto != null) {
            return esAdulto;
        }
        // Conserva la clasificación de los tipos de entrada anteriores.
        return NOMBRE_ADULTO.matcher(nombre).find();
    }

    @OneToMany(mappedBy = "tipoEntrada", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Boleta> boletas = new ArrayList<>();
}
