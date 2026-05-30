package com.solem.ginko.prueba_tecnica.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReportePagosResponse(
    String idProveedor,
    LocalDate desde,
    LocalDate hasta,
    BigDecimal totalPagado
) {}
