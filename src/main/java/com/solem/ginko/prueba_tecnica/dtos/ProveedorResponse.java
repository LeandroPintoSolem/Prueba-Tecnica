package com.solem.ginko.prueba_tecnica.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de un proveedor para respuestas de la API")
public record ProveedorResponse(

    @Schema(description = "Identificador único del proveedor (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    String id,

    @Schema(description = "Razón social del proveedor", example = "Constructora Acme S.A.")
    String razonSocial,

    @Schema(description = "Identificador tributario único del proveedor", example = "76123456")
    Long idTributario,

    @Schema(description = "Email de contacto del proveedor", example = "contacto@acme.cl")
    String email,

    @Schema(description = "Estado actual del proveedor", example = "ACTIVO", allowableValues = {"ACTIVO", "INACTIVO"})
    String estado
) {}
