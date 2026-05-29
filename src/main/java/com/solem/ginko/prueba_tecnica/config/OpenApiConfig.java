package com.solem.ginko.prueba_tecnica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Prueba Técnica")
                        .description("API para la gestión de Proveedores y Órdenes de Pago")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Leandro Pinto")
                                .email("leandro.pinto@solem.cl")));
    }
}
