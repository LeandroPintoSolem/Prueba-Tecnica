package com.solem.ginko.prueba_tecnica.mappers;

import org.springframework.stereotype.Component;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.UpdateProveedorRequest;
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

    public Proveedor toEntity(ProveedorRequest request) {
        return Proveedor.builder()
                .razonSocial(request.razonSocial())
                .idTributario(request.idTributario())
                .email(request.email())
                .estado(request.estado())
                .build();
    }

    public void updateEntity(Proveedor proveedor, UpdateProveedorRequest request) {
        proveedor.setRazonSocial(request.razonSocial());
        proveedor.setEmail(request.email());
    }

    public void updateEstado(Proveedor proveedor, UpdateEstadoProveedorRequest request) {
        proveedor.setEstado(request.estado());
    }
}
