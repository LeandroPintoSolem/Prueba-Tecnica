package com.solem.ginko.prueba_tecnica.services;

import java.util.UUID;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;

public interface ProveedorService {
    ProveedorResponse getProveedor(UUID id);
}
