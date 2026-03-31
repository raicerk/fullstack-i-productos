package com.duoc.productos.repository;

import com.duoc.productos.model.Productos;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductosRepository {

    private List<Productos> listaProductos = new ArrayList<>();

    public Productos guardar(Productos producto) {
        listaProductos.add(producto);
        return producto;
    }

    public List<Productos> listar() {
        return listaProductos;
    }

    public Productos buscarPorId(Integer id) {
        return listaProductos.stream().filter(producto -> producto.getId().equals(id)).findFirst().orElse(null);
    }

    public Productos actualizar(Integer id, Productos producto) {
        int posicion = 0;
        for (int i = 0; i < listaProductos.size(); i++) {
            if(listaProductos.get(i).getId().equals(id)) {
                posicion = i;
                break;
            }
        }

        Productos productoNuevo = new Productos();
        productoNuevo.setId(id);
        productoNuevo.setNombre(producto.getNombre());
        productoNuevo.setCantidad(producto.getCantidad());
        productoNuevo.setPrecio(producto.getPrecio());

        listaProductos.set(posicion, productoNuevo);
        return productoNuevo;

    }


    public void eliminar(Integer id) {
        listaProductos.removeIf(producto -> producto.getId().equals(id));
    }

}
