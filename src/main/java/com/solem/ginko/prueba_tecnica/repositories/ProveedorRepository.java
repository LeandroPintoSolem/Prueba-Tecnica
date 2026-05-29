package com.solem.ginko.prueba_tecnica.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
import com.solem.ginko.prueba_tecnica.models.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {

    List<Proveedor> findByEstado(EstadoProveedor estado);
}
