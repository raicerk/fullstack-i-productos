package com.duoc.productos.service;

import com.duoc.productos.client.CategoriaClient;
import com.duoc.productos.dto.CategoriaDTO;
import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.dto.ProductoRequest;
import com.duoc.productos.exception.CategoriaNotFoundException;
import com.duoc.productos.exception.ProductoNotFoundException;
import com.duoc.productos.model.Productos;
import com.duoc.productos.repository.ProductosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductosService {

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private CategoriaClient categoriaClient;

    public ProductoDTO guardar(ProductoRequest request) {
        validarCategoria(request.getCategoria());

        Productos producto = new Productos();
        producto.setNombre(request.getNombre());
        producto.setCantidad(request.getCantidad());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(request.getCategoria());

        log.info("Producto almacenado correctamente: " + producto);
        return convertirADTO(productosRepository.save(producto));
    }

    public List<ProductoDTO> listar() {
        return productosRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO buscarPorId(Integer id) {
        Productos producto = productosRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return convertirADTO(producto);
    }

    public ProductoDTO actualizar(Integer id, ProductoRequest request) {
        validarCategoria(request.getCategoria());

        Productos productoExistente = productosRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        productoExistente.setNombre(request.getNombre());
        productoExistente.setCantidad(request.getCantidad());
        productoExistente.setPrecio(request.getPrecio());
        productoExistente.setCategoria(request.getCategoria());

        return convertirADTO(productosRepository.save(productoExistente));
    }

    public void eliminar(Integer id) {
        productosRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
        productosRepository.deleteById(id);
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productosRepository.findProductosByNombreContainsIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Consulta las categorías válidas en Platzi y lanza excepción si la categoría no existe
    private void validarCategoria(String categoria) {
        List<CategoriaDTO> categorias = categoriaClient.obtenerCategorias();
        boolean existe = categorias.stream().anyMatch(c -> c.getName().equalsIgnoreCase(categoria));
        if (!existe) throw new CategoriaNotFoundException(categoria);
    }

    private ProductoDTO convertirADTO(Productos producto) {
        if (producto == null) return null;
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setCantidad(producto.getCantidad());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        return dto;
    }
}
