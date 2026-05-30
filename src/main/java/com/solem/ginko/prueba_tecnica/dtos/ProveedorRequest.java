package com.solem.ginko.prueba_tecnica.dtos;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProveedorRequest(

    @NotBlank
    String razonSocial,

    @NotNull
    Long idTributario,

    @NotBlank
    @Email
    String email,

    @NotNull
    EstadoProveedor estado
) {}
