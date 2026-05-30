package com.solem.ginko.prueba_tecnica.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.services.OrdenPagoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ordenes-pago")
public class OrdenPagoController {
    private final OrdenPagoService ordenPagoService;
    
    @PostMapping
    public ResponseEntity<OrdenPagoResponse> createOrdenPago(@Valid @RequestBody OrdenPagoRequest request) {
        OrdenPagoResponse created = ordenPagoService.createOrdenPago(request);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.idOrdenPago())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }
    
}
