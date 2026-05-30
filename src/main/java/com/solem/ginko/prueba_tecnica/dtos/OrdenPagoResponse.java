package com.solem.ginko.prueba_tecnica.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;

public record OrdenPagoResponse(
    String idOrdenPago,
    String idProveedor,
    BigDecimal monto,
    String concepto,
    LocalDateTime fechaCreacion,
    EstadoOrdenPago estado
){}
