package com.solem.ginko.prueba_tecnica.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EstadoOrdenPagoTest {

    @Test
    void borrador_puedeTransicionarA_aprobada() {
        assertTrue(EstadoOrdenPago.BORRADOR.puedeTransicionarA(EstadoOrdenPago.APROBADA));
    }

    @Test
    void borrador_puedeTransicionarA_rechazada() {
        assertTrue(EstadoOrdenPago.BORRADOR.puedeTransicionarA(EstadoOrdenPago.RECHAZADA));
    }

    @Test
    void aprobada_puedeTransicionarA_pagada() {
        assertTrue(EstadoOrdenPago.APROBADA.puedeTransicionarA(EstadoOrdenPago.PAGADA));
    }

    @Test
    void borrador_noPuedeTransicionarA_pagada() {
        assertFalse(EstadoOrdenPago.BORRADOR.puedeTransicionarA(EstadoOrdenPago.PAGADA));
    }

    @Test
    void aprobada_noPuedeVolverA_borrador() {
        assertFalse(EstadoOrdenPago.APROBADA.puedeTransicionarA(EstadoOrdenPago.BORRADOR));
    }

    @Test
    void rechazada_esEstadoTerminal() {
        for (EstadoOrdenPago siguiente : EstadoOrdenPago.values()) {
            assertFalse(
                EstadoOrdenPago.RECHAZADA.puedeTransicionarA(siguiente),
                "RECHAZADA no debe poder transicionar a " + siguiente
            );
        }
    }

    @Test
    void pagada_esEstadoTerminal() {
        for (EstadoOrdenPago siguiente : EstadoOrdenPago.values()) {
            assertFalse(
                EstadoOrdenPago.PAGADA.puedeTransicionarA(siguiente),
                "PAGADA no debe poder transicionar a " + siguiente
            );
        }
    }

    @Test
    void ningunEstado_puedeTransicionarASiMismo() {
        for (EstadoOrdenPago estado : EstadoOrdenPago.values()) {
            assertFalse(
                estado.puedeTransicionarA(estado),
                estado + " no debe poder transicionar a sí mismo"
            );
        }
    }
}
