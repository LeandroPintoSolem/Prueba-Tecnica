package com.solem.ginko.prueba_tecnica.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solem.ginko.prueba_tecnica.models.OrdenPago;

public interface OrdenPagoRepository extends JpaRepository<OrdenPago, UUID> {
    
}
