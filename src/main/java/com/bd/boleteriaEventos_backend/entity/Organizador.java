package com.bd.boleteriaEventos_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "ORGANIZADORES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORGANIZADOR")
    private Long idOrganizador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @Column(name = "NIT")
    private String nit;

    @Column(name = "RAZON_SOCIAL")
    private String razonSocial;

    @Column(name = "NOMBRE_COMERCIAL")
    private String nombreComercial;

    @Lob
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "SITIO_WEB")
    private String sitioWeb;

    @Column(name = "TELEFONO_CONTACTO")
    private String telefonoContacto;

    @Column(name = "VERIFICADO", columnDefinition = "NUMBER(1)")
    private Boolean verificado;

    @OneToMany(mappedBy = "organizador", cascade = CascadeType.ALL)
    private List<Evento> eventos;
}
