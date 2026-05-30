package com.solem.ginko.prueba_tecnica.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos editables de un proveedor existente. El idTributario y el estado no se editan por este endpoint.")
public record UpdateProveedorRequest(

    @Schema(description = "Nueva razón social del proveedor", example = "Constructora Acme S.A.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String razonSocial,

    @Schema(description = "Nuevo email de contacto del proveedor", example = "contacto@acme.cl", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    String email
) {}
