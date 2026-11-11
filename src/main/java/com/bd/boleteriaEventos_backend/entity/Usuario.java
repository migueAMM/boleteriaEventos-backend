package com.bd.boleteriaEventos_backend.entity;

import com.bd.boleteriaEventos_backend.enums.Genero;
import com.bd.boleteriaEventos_backend.enums.Rol;
import com.bd.boleteriaEventos_backend.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "USUARIOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DOCUMENTO")
    private TipoDocumento tipoDocumento;

    @Column(name = "NUMERO_DOCUMENTO")
    private String numeroDocumento;

    @Column(name = "NOMBRES")
    private String nombres;

    @Column(name = "APELLIDOS")
    private String apellidos;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENERO")
    private Genero genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROL")
    private Rol rol;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Column(name = "ACTIVO", columnDefinition = "NUMBER(1)")
    private Boolean activo;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Organizador> organizadores;

    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL)
    private List<OrdenCompra> ordenesCompra;
}
