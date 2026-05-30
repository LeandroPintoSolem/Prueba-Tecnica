package com.solem.ginko.prueba_tecnica.dtos;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear una nueva orden de pago. El estado inicial es siempre BORRADOR y no se acepta del cliente.")
public record OrdenPagoRequest(

    @Schema(description = "Identificador del proveedor (UUID) al que pertenece esta orden. El proveedor debe existir y estar ACTIVO.", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @UUID(message = "El id de proveedor debe tener el formato UUID")
    String idProveedor,

    @Schema(description = "Monto de la orden. Debe ser estrictamente mayor que cero.", example = "1500000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "El monto debe ser mayor que 0")
    BigDecimal monto,

    @Schema(description = "Descripción de la orden de pago. Máximo 250 caracteres.", example = "Compra de materiales de construcción - Lote A23", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 250)
    @NotBlank
    @Size(max = 250, message = "El largo máximo permitido para el concepto es de 250 caracteres")
    String concepto
) {}
