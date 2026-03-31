package com.duoc.productos.service;


import com.duoc.productos.model.Productos;
import com.duoc.productos.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductosService {
    @Autowired
    private ProductosRepository productosRepository;

    public Productos guardar(Productos productos) {
        return productosRepository.guardar(productos);
    }

    public List<Productos> listar() {
        return productosRepository.listar();
    }

    public Productos buscarPorId(Integer id) {
        return productosRepository.buscarPorId(id);
    }

    public Productos actualizar(Integer id, Productos productos) {
        return productosRepository.actualizar(id, productos);
    }

    public void eliminar(Integer id) {
        productosRepository.eliminar(id);
    }
}
