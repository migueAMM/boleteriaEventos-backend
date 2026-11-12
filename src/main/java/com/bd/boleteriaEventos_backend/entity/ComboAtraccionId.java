package com.bd.boleteriaEventos_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboAtraccionId implements Serializable {

    @Column(name = "ID_COMBO")
    private Long idCombo;

    @Column(name = "ID_ATRACCION")
    private Long idAtraccion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComboAtraccionId that = (ComboAtraccionId) o;
        return Objects.equals(idCombo, that.idCombo) &&
                Objects.equals(idAtraccion, that.idAtraccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCombo, idAtraccion);
    }
}
