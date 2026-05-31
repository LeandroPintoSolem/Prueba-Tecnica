package com.solem.ginko.prueba_tecnica.controllers;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.solem.ginko.prueba_tecnica.dtos.ReportePagosResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoProveedorRequest;
import com.solem.ginko.prueba_tecnica.dtos.UpdateProveedorRequest;
import com.solem.ginko.prueba_tecnica.exceptions.ErrorResponse;
import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
import com.solem.ginko.prueba_tecnica.services.OrdenPagoService;
import com.solem.ginko.prueba_tecnica.services.ProveedorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/proveedores")
@Tag(name = "Proveedores", description = "Gestión de proveedores: alta, consulta, listado paginado, actualización de datos, cambio de estado y reporte de pagos.")
public class ProveedorController {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProveedorService proveedorService;
    private final OrdenPagoService ordenPagoService;

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener proveedor por id",
        description = "Retorna los datos completos de un proveedor identificado por su UUID."
    )
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado")
    @ApiResponse(
        responseCode = "404",
        description = "Proveedor no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "El id no tiene formato UUID válido",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ProveedorResponse> getProveedor(
        @Parameter(description = "UUID del proveedor", example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable UUID id
    ) {
        log.info("[getProveedor] IN - id: " + id);
        return ResponseEntity.ok(proveedorService.getProveedor(id));
    }

    @GetMapping
    @Operation(
        summary = "Listar proveedores paginados",
        description = "Retorna una página de proveedores. Permite filtrar opcionalmente por estado. Si no hay resultados, devuelve una página vacía (no 404). El tamaño de página máximo es 50; valores fuera de rango se normalizan silenciosamente."
    )
    @ApiResponse(responseCode = "200", description = "Página de proveedores (posiblemente vacía)")
    public ResponseEntity<PageResponse<ProveedorResponse>> listarProveedores(
            @Parameter(description = "Filtro opcional por estado del proveedor", example = "ACTIVO")
            @RequestParam(required = false) EstadoProveedor estado,
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página (máx 50)", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("[listarProveedores] IN - estado: " + estado);
        int safePage = Math.max(0, page);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        return ResponseEntity.ok(proveedorService.listProveedores(estado, pageable));
    }

    @PostMapping
    @Operation(
        summary = "Crear un nuevo proveedor",
        description = "Crea un proveedor nuevo. El idTributario debe ser único. Devuelve 201 con el recurso creado y un header Location apuntando al nuevo recurso."
    )
    @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente")
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos en el request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe un proveedor con ese idTributario",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
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
    @Operation(
        summary = "Actualizar datos de un proveedor",
        description = "Actualiza la razón social y el email de un proveedor existente. El idTributario no se puede modificar por este endpoint (es inmutable). Para cambiar el estado, usar PATCH /{id}/estado."
    )
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado")
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos en el request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Proveedor no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ProveedorResponse> updateProveedor(
        @Parameter(description = "UUID del proveedor a actualizar", example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable UUID id,
        @Valid @RequestBody UpdateProveedorRequest newProveedor
    ) {
        log.info("[updateProveedor] IN - id: " + id);
        ProveedorResponse proveedor = proveedorService.updateProveedor(id, newProveedor);

        return ResponseEntity.ok(proveedor);
    }

    @PatchMapping("/{id}/estado")
    @Operation(
        summary = "Cambiar estado de un proveedor",
        description = "Cambia el estado de un proveedor (ACTIVO o INACTIVO). Endpoint separado del PUT para mantener la semántica REST de modificación parcial de un sub-recurso."
    )
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(
        responseCode = "400",
        description = "Estado inválido en el request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Proveedor no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ProveedorResponse> updateEstadoProveedor(
        @Parameter(description = "UUID del proveedor", example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable UUID id,
        @Valid @RequestBody UpdateEstadoProveedorRequest request
    ) {
        log.info("[updateEstadoProveedor] IN - id: {} - estado: {}", id, request.estado());
        ProveedorResponse proveedor = proveedorService.updateEstadoProveedor(id, request);

        return ResponseEntity.ok(proveedor);
    }

    @GetMapping("/{id}/reporte-pagos")
    @Operation(
        summary = "Reporte de total pagado a un proveedor en un rango de fechas",
        description = "Retorna la suma de los montos de todas las órdenes en estado PAGADA del proveedor, cuya fechaCreacion caiga dentro del rango [desde, hasta] (inclusivo en ambos extremos). Si no hay órdenes que matcheen, retorna 0."
    )
    @ApiResponse(responseCode = "200", description = "Reporte calculado correctamente")
    @ApiResponse(
        responseCode = "404",
        description = "Proveedor no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "422",
        description = "La fecha 'desde' es posterior a la fecha 'hasta'",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Formato de fecha inválido (debe ser ISO 8601: yyyy-MM-dd)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ReportePagosResponse> reporteTotalPagado(
        @Parameter(description = "UUID del proveedor", example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable UUID id,
        @Parameter(description = "Fecha de inicio del rango (inclusiva), formato ISO 8601", example = "2026-05-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @Parameter(description = "Fecha de fin del rango (inclusiva), formato ISO 8601", example = "2026-05-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        log.info("[reporteTotalPagado] IN - idProveedor: {} - desde: {} - hasta: {}", id, desde, hasta);
        return ResponseEntity.ok(ordenPagoService.reporteTotalPagado(id, desde, hasta));
    }
}
