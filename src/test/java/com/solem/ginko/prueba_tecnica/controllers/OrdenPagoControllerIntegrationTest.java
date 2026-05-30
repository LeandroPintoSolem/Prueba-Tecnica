package com.solem.ginko.prueba_tecnica.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.solem.ginko.prueba_tecnica.models.EstadoProveedor;
import com.solem.ginko.prueba_tecnica.models.Proveedor;
import com.solem.ginko.prueba_tecnica.repositories.ProveedorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrdenPagoControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProveedorRepository proveedorRepository;

    private Proveedor proveedorActivo;

    @BeforeEach
    void setUp() {
        proveedorActivo = proveedorRepository.save(Proveedor.builder()
                .razonSocial("ACME Test")
                .idTributario(987654321L)
                .email("acme@test.com")
                .estado(EstadoProveedor.ACTIVO)
                .build());
    }

    @Test
    void crearOrdenPago_conProveedorActivoYDatosValidos_devuelve201ConBodyYLocation() throws Exception {
        String body = """
            {
              "idProveedor": "%s",
              "monto": 1500,
              "concepto": "Compra de servicios"
            }
            """.formatted(proveedorActivo.getIdProveedor());

        mockMvc.perform(post("/api/v1/ordenes-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.idOrdenPago").exists())
            .andExpect(jsonPath("$.idProveedor").value(proveedorActivo.getIdProveedor().toString()))
            .andExpect(jsonPath("$.monto").value(1500))
            .andExpect(jsonPath("$.concepto").value("Compra de servicios"))
            .andExpect(jsonPath("$.estado").value("BORRADOR"))
            .andExpect(jsonPath("$.fechaCreacion").exists());
    }

    @Test
    void crearOrdenPago_conProveedorInexistente_devuelve404() throws Exception {
        String body = """
            {
              "idProveedor": "%s",
              "monto": 1500,
              "concepto": "Compra"
            }
            """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/ordenes-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void crearOrdenPago_conProveedorInactivo_devuelve422() throws Exception {
        proveedorActivo.setEstado(EstadoProveedor.INACTIVO);
        proveedorRepository.save(proveedorActivo);

        String body = """
            {
              "idProveedor": "%s",
              "monto": 1500,
              "concepto": "Compra"
            }
            """.formatted(proveedorActivo.getIdProveedor());

        mockMvc.perform(post("/api/v1/ordenes-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void crearOrdenPago_conMontoCero_devuelve400() throws Exception {
        String body = """
            {
              "idProveedor": "%s",
              "monto": 0,
              "concepto": "Compra"
            }
            """.formatted(proveedorActivo.getIdProveedor());

        mockMvc.perform(post("/api/v1/ordenes-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void crearOrdenPago_conConceptoExcedeLimite_devuelve400() throws Exception {
        String conceptoLargo = "x".repeat(251);
        String body = """
            {
              "idProveedor": "%s",
              "monto": 1500,
              "concepto": "%s"
            }
            """.formatted(proveedorActivo.getIdProveedor(), conceptoLargo);

        mockMvc.perform(post("/api/v1/ordenes-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
