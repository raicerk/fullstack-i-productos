package com.duoc.productos.controller;

import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.dto.ProductoRequest;
import com.duoc.productos.service.ProductosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request) {
        return new ResponseEntity<>(productosService.guardar(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return new ResponseEntity<>(productosService.listar(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Integer id) {
        try{
            return new ResponseEntity<>(productosService.buscarPorId(id), HttpStatus.OK);
        }catch(NullPointerException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoRequest request) {
        try {
            return new ResponseEntity<>(productosService.actualizar(id, request), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productosService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}