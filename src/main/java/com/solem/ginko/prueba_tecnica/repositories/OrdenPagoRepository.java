package com.solem.ginko.prueba_tecnica.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;
import com.solem.ginko.prueba_tecnica.models.OrdenPago;

public interface OrdenPagoRepository extends JpaRepository<OrdenPago, UUID> {
    @Query("""
        SELECT o FROM OrdenPago o
        WHERE (:estado IS NULL OR o.estado = :estado)
            AND (:idProveedor IS NULL OR o.proveedor.idProveedor = :idProveedor)
    """)
    Page<OrdenPago> findByFilters(
        @Param("estado") EstadoOrdenPago estado,
        @Param("idProveedor") UUID idProveedor,
        Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(o.monto), 0) FROM OrdenPago o
        WHERE o.proveedor.idProveedor = :idProveedor
            AND o.estado = com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago.PAGADA
            AND o.fechaCreacion >= :desde
            AND o.fechaCreacion <= :hasta
    """)
    BigDecimal sumTotalPagadoByProveedorEnRango(
        @Param("idProveedor") UUID idProveedor,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );
}
