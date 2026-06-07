package com.duoc.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos del producto retornados por la API")
public class ProductoDTO {

    @Schema(description = "ID único del producto generado por la base de datos", example = "1")
    private Integer id;

    @Schema(description = "Nombre del producto", example = "Teclado Gamer Razer")
    private String nombre;

    @Schema(description = "Cantidad disponible en inventario", example = "10")
    private Integer cantidad;

    @Schema(description = "Precio del producto en pesos chilenos", example = "39990")
    private Integer precio;

    @Schema(description = "Categoría del producto", example = "Electronics")
    private String categoria;
}