package com.duoc.productos.service;


import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.dto.ProductoRequest;
import com.duoc.productos.model.Productos;
import com.duoc.productos.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductosService {
    @Autowired
    private ProductosRepository productosRepository;

    public ProductoDTO guardar(ProductoRequest request) {
        Productos producto = new Productos();
        producto.setNombre(request.getNombre());
        producto.setCantidad(request.getCantidad());
        producto.setPrecio(request.getPrecio());

        Productos guardado = productosRepository.save(producto);
        return convertirADTO(guardado);
    }


    public List<ProductoDTO> listar() {
        return productosRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }


    public ProductoDTO buscarPorId(Integer id) {
        Productos producto = productosRepository.findById(id).orElseThrow(() -> new NullPointerException("No existe el producto con el id: " + id));
        return convertirADTO(producto);
    }


    public ProductoDTO actualizar(Integer id, ProductoRequest request) {
        Productos productoExistente = productosRepository.findById(id).orElseThrow(() -> new NullPointerException("Producto no encontrado"));

        productoExistente.setNombre(request.getNombre());
        productoExistente.setCantidad(request.getCantidad());
        productoExistente.setPrecio(request.getPrecio());

        Productos actualizado = productosRepository.save(productoExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Integer id) {
        productosRepository.deleteById(id);
    }

    // MÉTODO HELPER: Para no repetir código de conversión
    private ProductoDTO convertirADTO(Productos producto) {
        if(producto == null) { return null; }
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setCantidad(producto.getCantidad());
        dto.setPrecio(producto.getPrecio());
        return dto;
    }
}
