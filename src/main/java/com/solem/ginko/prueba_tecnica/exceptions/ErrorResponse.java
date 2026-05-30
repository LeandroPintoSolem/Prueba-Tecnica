package com.solem.ginko.prueba_tecnica.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cuerpo estándar de respuesta de error")
public record ErrorResponse(

    @Schema(description = "Código HTTP del error", example = "404")
    int status,

    @Schema(description = "Mensaje descriptivo del error", example = "Proveedor no encontrado: 550e8400-e29b-41d4-a716-446655440000")
    String msg
) {}
