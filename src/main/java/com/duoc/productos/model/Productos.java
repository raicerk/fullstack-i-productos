package com.duoc.productos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Productos {

    @Positive(message = "Debe ser mayor a cero")
    private Integer Id;

    @NotNull(message = "No puede ser nulo")
    @NotBlank(message = "No puede estar vacio")
    private String Nombre;

    @Positive(message = "Debe ser mayor a cero")
    private Integer Cantidad;

    @Positive(message = "Debe ser mayor a cero")
    private Integer Precio;
}
