package com.solem.ginko.prueba_tecnica.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de una orden de pago para respuestas de la API")
public record OrdenPagoResponse(

    @Schema(description = "Identificador único de la orden de pago (UUID)", example = "9b2d4e1a-3c5f-4a8e-9d6b-7c8f2a1b3d4e")
    String idOrdenPago,

    @Schema(description = "Identificador del proveedor al que pertenece la orden", example = "550e8400-e29b-41d4-a716-446655440000")
    String idProveedor,

    @Schema(description = "Monto de la orden de pago", example = "1500000")
    BigDecimal monto,

    @Schema(description = "Descripción de la orden de pago", example = "Compra de materiales de construcción - Lote A23")
    String concepto,

    @Schema(description = "Fecha y hora de creación de la orden (ISO 8601)", example = "2026-05-30T14:25:00")
    LocalDateTime fechaCreacion,

    @Schema(description = "Estado actual de la orden", example = "BORRADOR")
    EstadoOrdenPago estado
){}
