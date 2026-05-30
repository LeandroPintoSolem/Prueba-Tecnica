package com.solem.ginko.prueba_tecnica.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solem.ginko.prueba_tecnica.dtos.ListOrdenesPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.dtos.PageResponse;
import com.solem.ginko.prueba_tecnica.dtos.ReportePagosResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoOrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.exceptions.BusinessException;
import com.solem.ginko.prueba_tecnica.exceptions.NotFoundException;
import com.solem.ginko.prueba_tecnica.mappers.OrdenPagoMapper;
import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrdenPagoResponse> listOrdenesPago(ListOrdenesPagoRequest request, Pageable pageable) {
        Page<OrdenPago> page = ordenPagoRepository.findByFilters(request.estado(), request.idProveedor(), pageable);

        return PageResponse.from(page.map(ordenPagoMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenPagoResponse getOrdenPago(UUID id) {
        OrdenPago orden = ordenPagoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orden de pago no encontrada: " + id));

        return ordenPagoMapper.toResponse(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportePagosResponse reporteTotalPagado(UUID idProveedor, LocalDate desde, LocalDate hasta) {
        if (!proveedorRepository.existsById(idProveedor)) {
            throw new NotFoundException("Proveedor no encontrado: " + idProveedor);
        }

        if (desde.isAfter(hasta)) {
            throw new BusinessException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        LocalDateTime desdeInicio = desde.atStartOfDay();
        LocalDateTime hastaFin = hasta.atTime(LocalTime.MAX);

        BigDecimal total = ordenPagoRepository.sumTotalPagadoByProveedorEnRango(idProveedor, desdeInicio, hastaFin);

        return new ReportePagosResponse(idProveedor.toString(), desde, hasta, total);
    }

    @Override
    @Transactional
    public OrdenPagoResponse updateEstadoOrdenPago(UUID id, UpdateEstadoOrdenPagoRequest request) {
        OrdenPago orden = ordenPagoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orden de pago no encontrada: " + id));

        EstadoOrdenPago estadoActual = orden.getEstado();
        EstadoOrdenPago estadoNuevo = request.estado();

        if (!estadoActual.puedeTransicionarA(estadoNuevo)) {
            throw new BusinessException(
                String.format("Transición de estado inválida: no se puede pasar de %s a %s", estadoActual, estadoNuevo)
            );
        }

        ordenPagoMapper.updateEstado(orden, request);

        return ordenPagoMapper.toResponse(orden);
    }
}
