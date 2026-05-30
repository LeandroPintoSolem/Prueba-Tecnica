package com.solem.ginko.prueba_tecnica.models;

import java.util.Map;
import java.util.Set;

public enum EstadoOrdenPago {
    BORRADOR,
    APROBADA,
    RECHAZADA,
    PAGADA;

    private static final Map<EstadoOrdenPago, Set<EstadoOrdenPago>> TRANSICIONES_VALIDAS = Map.of(
        BORRADOR, Set.of(APROBADA, RECHAZADA),
        APROBADA, Set.of(PAGADA)
    );

    public boolean puedeTransicionarA(EstadoOrdenPago siguiente) {
        return TRANSICIONES_VALIDAS.getOrDefault(this, Set.of()).contains(siguiente);
    }
}
