package com.duoc.productos.controller;


import com.duoc.productos.model.Productos;
import com.duoc.productos.service.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController {
    @Autowired
    private ProductosService productosService;

    @PostMapping
    public ResponseEntity<Productos> guardar(@RequestBody Productos productos) {
         return ResponseEntity.status(201).body(productosService.guardar(productos));
    }

    @GetMapping
    public ResponseEntity<List<Productos>> listar() {
        return ResponseEntity.status(200).body(productosService.listar());
    }

    @GetMapping("{id}")
    public ResponseEntity<Productos> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(productosService.buscarPorId(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<Productos> actualizar(@PathVariable Integer id, @RequestBody Productos productos) {
        return ResponseEntity.status(200).body(productosService.actualizar(id, productos));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        productosService.eliminar(id);
        return ResponseEntity.status(200).body("Producto eliminado");
    }
}
