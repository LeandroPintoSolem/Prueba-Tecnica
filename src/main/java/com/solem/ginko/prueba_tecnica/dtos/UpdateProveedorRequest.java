package com.solem.ginko.prueba_tecnica.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateProveedorRequest(

    @NotBlank
    String razonSocial,

    @NotBlank
    @Email
    String email
) {}
