package com.solem.ginko.prueba_tecnica.dtos;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Wrapper de respuesta paginada")
public record PageResponse<T>(

    @Schema(description = "Lista de elementos de la página actual")
    List<T> content,

    @Schema(description = "Número de página actual (0-indexed: la primera página es 0)", example = "0")
    int page,

    @Schema(description = "Cantidad de elementos por página", example = "20")
    int size,

    @Schema(description = "Total de elementos que cumplen los filtros (a lo largo de todas las páginas)", example = "47")
    long totalElements,

    @Schema(description = "Total de páginas disponibles", example = "3")
    int totalPages
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
