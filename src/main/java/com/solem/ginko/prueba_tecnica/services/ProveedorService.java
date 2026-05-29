package com.solem.ginko.prueba_tecnica.services;

import java.util.List;
import java.util.UUID;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

public interface ProveedorService {
    ProveedorResponse getProveedor(UUID id);
    List<ProveedorResponse> getProveedoresByEstado(EstadoProveedor estado);
}
