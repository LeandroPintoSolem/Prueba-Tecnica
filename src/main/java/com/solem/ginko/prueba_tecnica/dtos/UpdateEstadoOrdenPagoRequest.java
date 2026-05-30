package com.solem.ginko.prueba_tecnica.dtos;

import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;

import jakarta.validation.constraints.NotNull;

public record UpdateEstadoOrdenPagoRequest(
    @NotNull EstadoOrdenPago estado
) {}
