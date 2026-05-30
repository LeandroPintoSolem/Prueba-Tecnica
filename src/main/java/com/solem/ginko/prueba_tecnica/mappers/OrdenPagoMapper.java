package com.solem.ginko.prueba_tecnica.mappers;

import org.springframework.stereotype.Component;

import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoOrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;
import com.solem.ginko.prueba_tecnica.models.OrdenPago;
import com.solem.ginko.prueba_tecnica.models.Proveedor;

@Component
public class OrdenPagoMapper {

    public OrdenPagoResponse toResponse(OrdenPago orden) {

        return new OrdenPagoResponse(
            orden.getIdOrdenPago().toString(),
            orden.getProveedor().getIdProveedor().toString(),
            orden.getMonto(),
            orden.getConcepto(),
            orden.getFechaCreacion(),
            orden.getEstado()
        );
    }

    public OrdenPago toEntity(OrdenPagoRequest orden, Proveedor proveedor) {
        return OrdenPago.builder()
            .proveedor(proveedor)
            .monto(orden.monto())
            .concepto(orden.concepto())
            .estado(EstadoOrdenPago.BORRADOR)
            .build();
    }

    public void updateEstado(OrdenPago orden, UpdateEstadoOrdenPagoRequest request) {
        orden.setEstado(request.estado());
    }
}
