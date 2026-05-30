package com.solem.ginko.prueba_tecnica.services;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.solem.ginko.prueba_tecnica.dtos.ListOrdenesPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.dtos.PageResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoOrdenPagoRequest;

public interface OrdenPagoService {
    OrdenPagoResponse createOrdenPago(OrdenPagoRequest orden);
    PageResponse<OrdenPagoResponse> listOrdenesPago(ListOrdenesPagoRequest request, Pageable pageable);
    OrdenPagoResponse getOrdenPago(UUID id);
    OrdenPagoResponse updateEstadoOrdenPago(UUID id, UpdateEstadoOrdenPagoRequest request);
}
