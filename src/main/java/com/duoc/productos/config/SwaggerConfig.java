package com.duoc.productos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Productos")
                        .description("API REST para la gestión de productos. Permite crear, listar, buscar, actualizar y eliminar productos. Las categorías son validadas contra la Platzi Fake Store API.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("DUOC UC - Fullstack I")
                                .url("https://www.duoc.cl")));
    }
}
