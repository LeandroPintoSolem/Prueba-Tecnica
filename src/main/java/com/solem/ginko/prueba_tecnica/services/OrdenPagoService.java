package com.solem.ginko.prueba_tecnica.services;

import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;

public interface OrdenPagoService {
    OrdenPagoResponse createOrdenPago(OrdenPagoRequest orden);
}
