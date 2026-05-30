package com.solem.ginko.prueba_tecnica.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
import com.solem.ginko.prueba_tecnica.models.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {

    Page<Proveedor> findByEstado(EstadoProveedor estado, Pageable pageable);
}
