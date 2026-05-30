package com.solem.ginko.prueba_tecnica.dtos;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrdenPagoRequest(
    @NotNull
    @UUID(message = "El id de proveedor debe tener el formato UUID")
    String idProveedor,

    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "El monto debe ser mayor que 0")
    BigDecimal monto,

    @NotBlank
    @Size(max = 250, message = "El largo máximo permitido para el concepto es de 250 caracteres")
    String concepto
) {}
