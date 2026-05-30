package com.solem.ginko.prueba_tecnica.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.exceptions.BusinessException;
import com.solem.ginko.prueba_tecnica.exceptions.NotFoundException;
import com.solem.ginko.prueba_tecnica.mappers.OrdenPagoMapper;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
import com.solem.ginko.prueba_tecnica.models.OrdenPago;
import com.solem.ginko.prueba_tecnica.models.Proveedor;
import com.solem.ginko.prueba_tecnica.repositories.OrdenPagoRepository;
import com.solem.ginko.prueba_tecnica.repositories.ProveedorRepository;
import com.solem.ginko.prueba_tecnica.services.OrdenPagoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenPagoServiceImpl implements OrdenPagoService {
    private final OrdenPagoRepository ordenPagoRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrdenPagoMapper ordenPagoMapper;

    @Override
    @Transactional
    public OrdenPagoResponse createOrdenPago(OrdenPagoRequest orden) {
        Proveedor proveedor = proveedorRepository.findById(UUID.fromString(orden.idProveedor()))
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrado."));

        if (proveedor.getEstado() != EstadoProveedor.ACTIVO)
            throw new BusinessException("Proveedor no se encuentra activo: " + proveedor.getIdProveedor());

        OrdenPago created = ordenPagoRepository.save(ordenPagoMapper.toEntity(orden, proveedor));

        return ordenPagoMapper.toResponse(created);
    }
}
