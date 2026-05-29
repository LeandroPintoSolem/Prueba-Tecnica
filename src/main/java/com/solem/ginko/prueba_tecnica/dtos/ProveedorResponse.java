package com.solem.ginko.prueba_tecnica.dtos;

public record ProveedorResponse(
    String id,
    String razonSocial,
    Long idTributario,
    String email,
    String estado
) {}
