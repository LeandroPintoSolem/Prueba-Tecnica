package com.solem.ginko.prueba_tecnica.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.solem.ginko.prueba_tecnica.dtos.ListOrdenesPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.dtos.PageResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoOrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;
import com.solem.ginko.prueba_tecnica.services.OrdenPagoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ordenes-pago")
public class OrdenPagoController {

    private static final int MAX_PAGE_SIZE = 50;

    private final OrdenPagoService ordenPagoService;
    
    @PostMapping
    public ResponseEntity<OrdenPagoResponse> createOrdenPago(@Valid @RequestBody OrdenPagoRequest request) {
        log.info(
            "[createOrdenPago] IN - idProveedor: {} - monto: {} - concepto: {}",
            request.idProveedor(),
            request.monto(),
            request.concepto()
        );

        OrdenPagoResponse created = ordenPagoService.createOrdenPago(request);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.idOrdenPago())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrdenPagoResponse> getOrdenPago(@PathVariable UUID id) {
        log.info("[getOrdenPago] IN - id: {}", id);
        return ResponseEntity.ok(ordenPagoService.getOrdenPago(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrdenPagoResponse>> listOrdenesPago(
        @RequestParam(required = false) EstadoOrdenPago estado,
        @RequestParam(required = false) UUID idProveedor,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("[listOrdenesPago] IN - estado: {} - idProveedor: {}", estado, idProveedor);
        int safePage = Math.max(0, page);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        var request = new ListOrdenesPagoRequest(estado, idProveedor);

        return ResponseEntity.ok(ordenPagoService.listOrdenesPago(request, pageable));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenPagoResponse> updateEstadoOrdenPago(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateEstadoOrdenPagoRequest request
    ) {
        log.info("[updateEstadoOrdenPago] IN - id: {} - estado: {}", id, request.estado());
        return ResponseEntity.ok(ordenPagoService.updateEstadoOrdenPago(id, request));
    }

}
