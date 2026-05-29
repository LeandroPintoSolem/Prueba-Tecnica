package com.solem.ginko.prueba_tecnica.exceptions;

public record ErrorResponse(
    int status,
    String msg
) {}
