package com.solem.ginko.prueba_tecnica.services;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.solem.ginko.prueba_tecnica.dtos.PageResponse;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.UpdateProveedorRequest;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

public interface ProveedorService {
    ProveedorResponse getProveedor(UUID id);
    PageResponse<ProveedorResponse> listProveedores(EstadoProveedor estado, Pageable pageable);
    ProveedorResponse createProveedor(ProveedorRequest proveedorRequest);
    ProveedorResponse updateProveedor(UUID id, UpdateProveedorRequest proveedorRequest);
    ProveedorResponse updateEstadoProveedor(UUID id, UpdateEstadoProveedorRequest request);
}
