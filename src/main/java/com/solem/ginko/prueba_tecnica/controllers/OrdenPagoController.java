package com.solem.ginko.prueba_tecnica.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.solem.ginko.prueba_tecnica.dtos.ListOrdenesPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.dtos.OrdenPagoResponse;
import com.solem.ginko.prueba_tecnica.dtos.PageResponse;
import com.solem.ginko.prueba_tecnica.dtos.UpdateEstadoOrdenPagoRequest;
import com.solem.ginko.prueba_tecnica.exceptions.ErrorResponse;
import com.solem.ginko.prueba_tecnica.models.EstadoOrdenPago;
import com.solem.ginko.prueba_tecnica.services.OrdenPagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Órdenes de Pago", description = "Gestión de órdenes de pago: creación, consulta, listado paginado con filtros y transición de estados según reglas de negocio.")
public class OrdenPagoController {

    private static final int MAX_PAGE_SIZE = 50;

    private final OrdenPagoService ordenPagoService;

    @PostMapping
    @Operation(
        summary = "Crear una nueva orden de pago",
        description = "Crea una orden de pago para un proveedor existente y ACTIVO. El estado inicial siempre es BORRADOR (no se acepta del cliente). Devuelve 201 con el recurso creado y header Location."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden de pago creada exitosamente"),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos en el request (monto, concepto, idProveedor mal formado)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "El proveedor referenciado no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "El proveedor existe pero no está en estado ACTIVO",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
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
    @Operation(
        summary = "Obtener orden de pago por id",
        description = "Retorna los datos completos de una orden de pago identificada por su UUID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(
            responseCode = "404",
            description = "Orden de pago no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "El id no tiene formato UUID válido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<OrdenPagoResponse> getOrdenPago(
        @Parameter(description = "UUID de la orden de pago", example = "9b2d4e1a-3c5f-4a8e-9d6b-7c8f2a1b3d4e")
        @PathVariable UUID id
    ) {
        log.info("[getOrdenPago] IN - id: {}", id);
        return ResponseEntity.ok(ordenPagoService.getOrdenPago(id));
    }

    @GetMapping
    @Operation(
        summary = "Listar órdenes de pago paginadas",
        description = "Retorna una página de órdenes de pago. Permite filtrar opcionalmente por estado y/o por proveedor. Si no hay resultados, devuelve una página vacía (no 404). Tamaño máximo 50."
    )
    @ApiResponse(responseCode = "200", description = "Página de órdenes (posiblemente vacía)")
    public ResponseEntity<PageResponse<OrdenPagoResponse>> listOrdenesPago(
        @Parameter(description = "Filtro opcional por estado de la orden", example = "BORRADOR")
        @RequestParam(required = false) EstadoOrdenPago estado,
        @Parameter(description = "Filtro opcional por UUID del proveedor", example = "550e8400-e29b-41d4-a716-446655440000")
        @RequestParam(required = false) UUID idProveedor,
        @Parameter(description = "Número de página (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Cantidad de elementos por página (máx 50)", example = "20")
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
    @Operation(
        summary = "Transicionar el estado de una orden de pago",
        description = """
            Aplica un cambio de estado a una orden de pago siguiendo las reglas de transición:
            - BORRADOR → APROBADA
            - BORRADOR → RECHAZADA
            - APROBADA → PAGADA

            Cualquier otra transición (incluyendo reaplicar el mismo estado, o transicionar desde estados terminales como PAGADA o RECHAZADA) se rechaza con 422 y un mensaje descriptivo.

            Maneja concurrencia con optimistic locking: si dos requests intentan modificar la misma orden simultáneamente, uno gana y el otro recibe 409.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(
            responseCode = "400",
            description = "Estado inválido en el request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Orden de pago no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto de concurrencia: la orden fue modificada por otro proceso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Transición de estado inválida según las reglas de negocio",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<OrdenPagoResponse> updateEstadoOrdenPago(
        @Parameter(description = "UUID de la orden de pago", example = "9b2d4e1a-3c5f-4a8e-9d6b-7c8f2a1b3d4e")
        @PathVariable UUID id,
        @Valid @RequestBody UpdateEstadoOrdenPagoRequest request
    ) {
        log.info("[updateEstadoOrdenPago] IN - id: {} - estado: {}", id, request.estado());
        return ResponseEntity.ok(ordenPagoService.updateEstadoOrdenPago(id, request));
    }

}
