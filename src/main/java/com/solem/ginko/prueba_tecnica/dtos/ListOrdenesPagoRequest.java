package com.solem.ginko.prueba_tecnica.dtos;

import java.util.UUID;

import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;

public record ListOrdenesPagoRequest(
    EstadoOrdenPago estado,
    UUID idProveedor
) {}
