package com.duoc.productos.controller;

import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.dto.ProductoRequest;
import com.duoc.productos.exception.ProductoNotFoundException;
import com.duoc.productos.service.ProductosService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para ProductosController.
 *
 * @WebMvcTest carga solo la capa web: el controlador, filtros y @RestControllerAdvice.
 * No levanta la base de datos, ni el contexto completo de Spring.
 *
 * MockMvc permite simular peticiones HTTP reales (POST, GET, PUT, DELETE)
 * y verificar el status code, los headers y el cuerpo JSON de la respuesta.
 *
 * ProductosService se reemplaza por @MockitoBean — no ejecuta lógica real.
 * Nota: En Spring Boot 4.x, @MockBean fue reemplazado por @MockitoBean.
 */
@WebMvcTest(ProductosController.class)
@DisplayName("Pruebas unitarias - ProductosController")
class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean registra el mock en el contexto de Spring (reemplaza el bean real)
    // En Spring Boot 4.x reemplaza al antiguo @MockBean de versiones anteriores
    @MockitoBean
    private ProductosService productosService;

    // ObjectMapper instanciado directamente — no necesita el contexto de Spring
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductoDTO productoDTO;
    private ProductoRequest requestValido;

    @BeforeEach
    void setUp() {
        productoDTO = new ProductoDTO();
        productoDTO.setId(1);
        productoDTO.setNombre("Teclado Gamer");
        productoDTO.setCantidad(10);
        productoDTO.setPrecio(39990);
        productoDTO.setCategoria("Electronics");

        requestValido = new ProductoRequest();
        requestValido.setNombre("Teclado Gamer");
        requestValido.setCantidad(10);
        requestValido.setPrecio(39990);
        requestValido.setCategoria("Electronics");
    }

    // =========================================================================
    // POST /api/v1/productos
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/productos: debería crear el producto y retornar 201")
    void shouldGuardarProductoYRetornar201() throws Exception {
        // Given: el servicio retorna el DTO del producto creado
        when(productosService.guardar(any(ProductoRequest.class))).thenReturn(productoDTO);

        // When + Then: se hace la petición y se verifica la respuesta
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())                          // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Teclado Gamer"))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.precio").value(39990))
                .andExpect(jsonPath("$.categoria").value("Electronics"));
    }

    @Test
    @DisplayName("POST /api/v1/productos: debería retornar 400 cuando los datos son inválidos")
    void shouldRetornar400CuandoDatosInvalidos() throws Exception {
        // Given: request con nombre vacío, cantidad negativa y precio nulo
        ProductoRequest requestInvalido = new ProductoRequest();
        requestInvalido.setNombre("");       // @NotBlank falla
        requestInvalido.setCantidad(-5);     // @Positive falla
        requestInvalido.setPrecio(null);     // @NotNull falla
        requestInvalido.setCategoria("");    // @NotBlank falla

        // When + Then: la validación de @Valid intercepta los errores antes de llegar al servicio
        // GlobalExceptionHandler retorna 400 con los mensajes de error
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());                      // 400
    }

    // =========================================================================
    // GET /api/v1/productos
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/productos: debería retornar la lista de productos y status 200")
    void shouldListarProductosYRetornar200() throws Exception {
        // Given: el servicio retorna una lista con un producto
        when(productosService.listar()).thenReturn(List.of(productoDTO));

        // When + Then
        mockMvc.perform(get("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                               // 200
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado Gamer"));
    }

    @Test
    @DisplayName("GET /api/v1/productos: debería retornar 204 cuando la lista está vacía")
    void shouldRetornar204CuandoListaVacia() throws Exception {
        // Given: el servicio retorna una lista vacía
        when(productosService.listar()).thenReturn(Collections.emptyList());

        // When + Then
        mockMvc.perform(get("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());                       // 204
    }

    // =========================================================================
    // GET /api/v1/productos/{id}
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/productos/{id}: debería retornar el producto y status 200 cuando el ID existe")
    void shouldBuscarPorIdYRetornar200() throws Exception {
        // Given: el servicio encuentra el producto con id=1
        when(productosService.buscarPorId(1)).thenReturn(productoDTO);

        // When + Then
        mockMvc.perform(get("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                               // 200
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Teclado Gamer"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id}: debería retornar 404 cuando el ID no existe")
    void shouldRetornar404CuandoIdNoExiste() throws Exception {
        // Given: el servicio lanza ProductoNotFoundException para id=99
        when(productosService.buscarPorId(99))
                .thenThrow(new ProductoNotFoundException(99));

        // When + Then: GlobalExceptionHandler captura la excepción y retorna 404
        mockMvc.perform(get("/api/v1/productos/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())                         // 404
                .andExpect(jsonPath("$.error").value("Producto no encontrado con id: 99"));
    }
}
