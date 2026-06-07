package com.duoc.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear o actualizar un producto")
public class ProductoRequest {

    @Schema(description = "Nombre del producto", example = "Teclado Gamer Razer")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Schema(description = "Cantidad disponible en inventario", example = "10")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;

    @Schema(description = "Precio del producto en pesos chilenos", example = "39990")
    @Positive(message = "El precio debe ser mayor a cero")
    @NotNull(message = "El precio es obligatorio")
    private Integer precio;

    @Schema(description = "Categoría del producto (debe existir en Platzi Fake Store API)", example = "Electronics")
    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;
}