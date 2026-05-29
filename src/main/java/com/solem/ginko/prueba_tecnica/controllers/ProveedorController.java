package com.solem.ginko.prueba_tecnica.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solem.ginko.prueba_tecnica.dtos.ProveedorResponse;
import com.solem.ginko.prueba_tecnica.services.ProveedorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> getProveedor(@PathVariable UUID id) {
        return ResponseEntity.ok(proveedorService.getProveedor(id));
    }
}
