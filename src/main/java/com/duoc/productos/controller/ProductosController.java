package com.duoc.productos.controller;

import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.dto.ProductoRequest;
import com.duoc.productos.service.ProductosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con la gestión de productos")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @Operation(summary = "Crear un producto", description = "Crea un nuevo producto. La categoría es validada contra la Platzi Fake Store API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request) {
        log.info("El request para crear un producto fue: " + request);
        return new ResponseEntity<>(productosService.guardar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar productos", description = "Retorna todos los productos. Si se proporciona el parámetro 'nombre', filtra por nombre (búsqueda parcial, sin distinción de mayúsculas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No hay productos que mostrar", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(
            @Parameter(description = "Nombre del producto para filtrar (opcional)") @RequestParam(required = false) String nombre) {
        List<ProductoDTO> productos;
        if (nombre != null) {
            productos = productosService.buscarPorNombre(nombre);
        } else {
            productos = productosService.listar();
        }

        if (productos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @Operation(summary = "Buscar producto por ID", description = "Retorna un producto específico según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(
            @Parameter(description = "ID del producto a buscar", required = true) @PathVariable Integer id) {
        return new ResponseEntity<>(productosService.buscarPorId(id), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un producto", description = "Actualiza todos los campos de un producto existente según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true) @PathVariable Integer id,
            @Valid @RequestBody ProductoRequest request) {
        return new ResponseEntity<>(productosService.actualizar(id, request), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true) @PathVariable Integer id) {
           productosService.eliminar(id);
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}