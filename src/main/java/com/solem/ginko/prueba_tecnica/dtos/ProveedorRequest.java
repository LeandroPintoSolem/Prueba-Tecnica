package com.solem.ginko.prueba_tecnica.dtos;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos requeridos para crear un nuevo proveedor")
public record ProveedorRequest(

    @Schema(description = "Razón social del proveedor", example = "Constructora Acme S.A.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String razonSocial,

    @Schema(description = "Identificador tributario único del proveedor (ej: RUT sin dígito verificador)", example = "76123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    Long idTributario,

    @Schema(description = "Email de contacto del proveedor", example = "contacto@acme.cl", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    String email,

    @Schema(description = "Estado inicial del proveedor", example = "ACTIVO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    EstadoProveedor estado
) {}
