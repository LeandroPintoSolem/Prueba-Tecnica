package com.solem.ginko.prueba_tecnica.services;

import java.util.List;
import java.util.UUID;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.UpdateProveedorRequest;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

public interface ProveedorService {
    ProveedorResponse getProveedor(UUID id);
    List<ProveedorResponse> getProveedoresByEstado(EstadoProveedor estado);
    ProveedorResponse createProveedor(ProveedorRequest proveedorRequest);
    ProveedorResponse updateProveedor(UUID id, UpdateProveedorRequest proveedorRequest);
    ProveedorResponse updateEstadoProveedor(UUID id, UpdateEstadoProveedorRequest request);
}
