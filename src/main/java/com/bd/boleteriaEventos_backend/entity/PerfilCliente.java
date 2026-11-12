package com.bd.boleteriaEventos_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PERFILES_CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PERFIL")
    private Long idPerfil;

    @Column(name = "DOCUMENTO", nullable = false, unique = true, length = 30)
    private String documento;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "TELEFONO", length = 30)
    private String telefono;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private UsuarioSistema usuario;

    @OneToMany(mappedBy = "perfilCliente", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();
}
