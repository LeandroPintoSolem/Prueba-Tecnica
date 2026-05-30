package com.solem.ginko.prueba_tecnica.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.solem.ginko.prueba_tecnica.dtos.PageResponse;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.UpdateProveedorRequest;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
import com.solem.ginko.prueba_tecnica.services.ProveedorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProveedorService proveedorService;

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> getProveedor(@PathVariable UUID id) {
        log.info("[getProveedor] IN - id: " + id);
        return ResponseEntity.ok(proveedorService.getProveedor(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProveedorResponse>> listarProveedores(
            @RequestParam(required = false) EstadoProveedor estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("[listarProveedores] IN - estado: " + estado);
        int safePage = Math.max(0, page);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        return ResponseEntity.ok(proveedorService.listProveedores(estado, pageable));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> createProveedor(@Valid @RequestBody ProveedorRequest request) {
        log.info("[createProveedor] IN");
        ProveedorResponse created = proveedorService.createProveedor(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> updateProveedor(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateProveedorRequest newProveedor
    ) {
        log.info("[updateProveedor] IN - id: " + id);
        ProveedorResponse proveedor = proveedorService.updateProveedor(id, newProveedor);

        return ResponseEntity.ok(proveedor);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProveedorResponse> updateEstadoProveedor(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateEstadoProveedorRequest request
    ) {
        log.info("[updateEstadoProveedor] IN - id: {} - estado: {}", id, request.estado());
        ProveedorResponse proveedor = proveedorService.updateEstadoProveedor(id, request);

        return ResponseEntity.ok(proveedor);
    }
}
