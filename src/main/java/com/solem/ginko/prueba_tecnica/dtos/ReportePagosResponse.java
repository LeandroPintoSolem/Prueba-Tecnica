package com.solem.ginko.prueba_tecnica.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reporte agregado del total pagado a un proveedor en un rango de fechas. Solo considera órdenes en estado PAGADA.")
public record ReportePagosResponse(

    @Schema(description = "Identificador del proveedor consultado", example = "550e8400-e29b-41d4-a716-446655440000")
    String idProveedor,

    @Schema(description = "Fecha de inicio del rango (inclusiva)", example = "2026-05-01")
    LocalDate desde,

    @Schema(description = "Fecha de fin del rango (inclusiva, hasta las 23:59:59.999)", example = "2026-05-31")
    LocalDate hasta,

    @Schema(description = "Suma de los montos de todas las órdenes PAGADAS del proveedor dentro del rango. 0 si no hay órdenes que matcheen.", example = "47500000")
    BigDecimal totalPagado
) {}
