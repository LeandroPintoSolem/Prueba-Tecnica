package com.solem.ginko.prueba_tecnica.mappers;

import org.springframework.stereotype.Component;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.models.Proveedor;

@Component
public class ProveedorMapper {

    public ProveedorResponse toResponse(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getIdProveedor().toString(),
                proveedor.getRazonSocial(),
                proveedor.getIdTributario(),
                proveedor.getEmail(),
                proveedor.getEstado().name()
        );
    }
}
