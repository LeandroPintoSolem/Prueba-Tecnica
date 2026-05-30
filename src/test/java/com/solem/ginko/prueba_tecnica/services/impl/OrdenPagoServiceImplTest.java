package com.solem.ginko.prueba_tecnica.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
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

@ExtendWith(MockitoExtension.class)
class OrdenPagoServiceImplTest {

    @Mock private OrdenPagoRepository ordenPagoRepository;
    @Mock private ProveedorRepository proveedorRepository;
    @Mock private OrdenPagoMapper ordenPagoMapper;

    @InjectMocks private OrdenPagoServiceImpl service;

    private Proveedor proveedor;
    private UUID idProveedor;
    private OrdenPagoRequest requestValido;

    @BeforeEach
    void setUp() {
        idProveedor = UUID.randomUUID();
        proveedor = Proveedor.builder()
                .idProveedor(idProveedor)
                .razonSocial("ACME")
                .idTributario(123456789L)
                .email("acme@example.com")
                .estado(EstadoProveedor.ACTIVO)
                .build();

        requestValido = new OrdenPagoRequest(
                idProveedor.toString(),
                BigDecimal.valueOf(1000),
                "Concepto válido"
        );
    }

    @Test
    void createOrdenPago_proveedorNoExiste_lanzaNotFoundException() {
        when(proveedorRepository.findById(idProveedor)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createOrdenPago(requestValido));
        verify(ordenPagoRepository, never()).save(any());
    }

    @Test
    void createOrdenPago_proveedorInactivo_lanzaBusinessException() {
        proveedor.setEstado(EstadoProveedor.INACTIVO);
        when(proveedorRepository.findById(idProveedor)).thenReturn(Optional.of(proveedor));

        BusinessException ex = assertThrows(
            BusinessException.class,
            () -> service.createOrdenPago(requestValido)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("activo"));
        verify(ordenPagoRepository, never()).save(any());
    }

    @Test
    void createOrdenPago_happyPath_persisteYDevuelveResponse() {
        OrdenPago entidadGuardada = OrdenPago.builder()
                .idOrdenPago(UUID.randomUUID())
                .proveedor(proveedor)
                .monto(BigDecimal.valueOf(1000))
                .concepto("Concepto válido")
                .estado(EstadoOrdenPago.BORRADOR)
                .build();

        OrdenPagoResponse responseEsperado = new OrdenPagoResponse(
                entidadGuardada.getIdOrdenPago().toString(),
                idProveedor.toString(),
                BigDecimal.valueOf(1000),
                "Concepto válido",
                null,
                EstadoOrdenPago.BORRADOR
        );

        when(proveedorRepository.findById(idProveedor)).thenReturn(Optional.of(proveedor));
        when(ordenPagoMapper.toEntity(requestValido, proveedor)).thenReturn(entidadGuardada);
        when(ordenPagoRepository.save(entidadGuardada)).thenReturn(entidadGuardada);
        when(ordenPagoMapper.toResponse(entidadGuardada)).thenReturn(responseEsperado);

        OrdenPagoResponse result = service.createOrdenPago(requestValido);

        assertEquals(responseEsperado, result);
        verify(ordenPagoRepository).save(entidadGuardada);
    }

    @Test
    void updateEstadoOrdenPago_ordenNoExiste_lanzaNotFoundException() {
        UUID idOrden = UUID.randomUUID();
        when(ordenPagoRepository.findById(idOrden)).thenReturn(Optional.empty());

        UpdateEstadoOrdenPagoRequest req = new UpdateEstadoOrdenPagoRequest(EstadoOrdenPago.APROBADA);

        assertThrows(NotFoundException.class, () -> service.updateEstadoOrdenPago(idOrden, req));
        verify(ordenPagoMapper, never()).updateEstado(any(), any());
    }

    @Test
    void updateEstadoOrdenPago_transicionInvalida_lanzaBusinessException() {
        UUID idOrden = UUID.randomUUID();
        OrdenPago orden = OrdenPago.builder()
                .idOrdenPago(idOrden)
                .proveedor(proveedor)
                .estado(EstadoOrdenPago.PAGADA)
                .build();
        when(ordenPagoRepository.findById(idOrden)).thenReturn(Optional.of(orden));

        UpdateEstadoOrdenPagoRequest req = new UpdateEstadoOrdenPagoRequest(EstadoOrdenPago.BORRADOR);

        BusinessException ex = assertThrows(
            BusinessException.class,
            () -> service.updateEstadoOrdenPago(idOrden, req)
        );

        assertTrue(ex.getMessage().contains("PAGADA"));
        assertTrue(ex.getMessage().contains("BORRADOR"));
        verify(ordenPagoMapper, never()).updateEstado(any(), any());
    }

    @Test
    void updateEstadoOrdenPago_transicionValida_delegaAlMapper() {
        UUID idOrden = UUID.randomUUID();
        OrdenPago orden = OrdenPago.builder()
                .idOrdenPago(idOrden)
                .proveedor(proveedor)
                .estado(EstadoOrdenPago.BORRADOR)
                .build();
        UpdateEstadoOrdenPagoRequest req = new UpdateEstadoOrdenPagoRequest(EstadoOrdenPago.APROBADA);

        when(ordenPagoRepository.findById(idOrden)).thenReturn(Optional.of(orden));

        service.updateEstadoOrdenPago(idOrden, req);

        verify(ordenPagoMapper).updateEstado(orden, req);
        verify(ordenPagoMapper).toResponse(orden);
    }
}
