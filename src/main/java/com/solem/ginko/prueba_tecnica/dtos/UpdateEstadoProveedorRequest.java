package com.solem.ginko.prueba_tecnica.dtos;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

import jakarta.validation.constraints.NotNull;

public record UpdateEstadoProveedorRequest(
    @NotNull EstadoProveedor estado
) {}
