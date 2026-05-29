package com.solem.ginko.prueba_tecnica.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.exceptions.NotFoundException;
import com.solem.ginko.prueba_tecnica.mappers.ProveedorMapper;
import com.solem.ginko.prueba_tecnica.models.Proveedor;
import com.solem.ginko.prueba_tecnica.repositories.ProveedorRepository;
import com.solem.ginko.prueba_tecnica.services.ProveedorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse getProveedor(UUID id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado: " + id));

        return proveedorMapper.toResponse(proveedor);
    }
}
