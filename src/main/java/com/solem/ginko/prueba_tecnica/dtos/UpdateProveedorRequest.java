package com.solem.ginko.prueba_tecnica.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateProveedorRequest {

    @NotBlank
    String razonSocial;

    @NotBlank
    @Email
    String email;
}
