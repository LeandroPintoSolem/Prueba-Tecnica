package com.solem.ginko.prueba_tecnica.dtos;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Cambio de estado de un proveedor")
public record UpdateEstadoProveedorRequest(

    @Schema(description = "Nuevo estado del proveedor", example = "INACTIVO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull EstadoProveedor estado
) {}
