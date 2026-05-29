package com.solem.ginko.prueba_tecnica.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateProveedorRequest;
import com.solem.ginko.prueba_tecnica.exceptions.NotFoundException;
import com.solem.ginko.prueba_tecnica.mappers.ProveedorMapper;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
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

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> getProveedoresByEstado(EstadoProveedor estado) {
        return proveedorRepository.findByEstado(estado).stream()
                .map(proveedorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProveedorResponse createProveedor(ProveedorRequest proveedorRequest) {
        Proveedor saved = proveedorRepository.save(proveedorMapper.toEntity(proveedorRequest));
        return proveedorMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProveedorResponse updateProveedor(UpdateProveedorRequest proveedorRequest, UUID id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado: " + id));

        proveedorMapper.updateEntity(proveedor, proveedorRequest);

        return proveedorMapper.toResponse(proveedor);
    }
}
