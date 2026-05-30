package com.solem.ginko.prueba_tecnica.dtos;

import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Cambio de estado de una orden de pago. Las transiciones válidas son: BORRADOR→APROBADA, BORRADOR→RECHAZADA, APROBADA→PAGADA. Cualquier otra transición es rechazada con 422.")
public record UpdateEstadoOrdenPagoRequest(

    @Schema(description = "Nuevo estado al que se desea transicionar la orden", example = "APROBADA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull EstadoOrdenPago estado
) {}
