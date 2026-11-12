package com.bd.boleteriaEventos_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BloqueoBoleteria {

    private final JdbcTemplate jdbcTemplate;

    void bloquearEscrituras() {
        // Oracle conserva el bloqueo hasta el commit o rollback.
        jdbcTemplate.execute("LOCK TABLE BOLETAS IN EXCLUSIVE MODE");
    }
}
