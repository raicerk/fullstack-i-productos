package com.duoc.productos.exception;

public class CategoriaNotFoundException extends RuntimeException {
    public CategoriaNotFoundException(String nombre) {
        super("Categoria no encontrada con nombre: " + nombre);
    }
}