package com.duoc.productos.service;

import com.duoc.productos.client.CategoriaClient;
import com.duoc.productos.dto.CategoriaDTO;
import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.dto.ProductoRequest;
import com.duoc.productos.exception.CategoriaNotFoundException;
import com.duoc.productos.exception.ProductoNotFoundException;
import com.duoc.productos.model.Productos;
import com.duoc.productos.repository.ProductosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductosService.
 *
 * Se usa @ExtendWith(MockitoExtension.class) para que Mockito inyecte los mocks
 * automáticamente sin levantar el contexto de Spring. Esto hace las pruebas
 * muy rápidas y completamente aisladas de la base de datos y APIs externas.
 *
 * Estructura de cada prueba: Given - When - Then (AAA: Arrange, Act, Assert)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ProductosService")
class ProductosServiceTest {

    // @Mock crea un objeto simulado — no llama a la BD ni a la API real
    @Mock
    private ProductosRepository productosRepository;

    @Mock
    private CategoriaClient categoriaClient;

    // @InjectMocks crea una instancia real de ProductosService
    // e inyecta los mocks anteriores como sus dependencias
    @InjectMocks
    private ProductosService productosService;

    // Objetos reutilizables entre tests
    private Productos productoGuardado;
    private ProductoRequest request;
    private CategoriaDTO categoriaValida;

    @BeforeEach
    void setUp() {
        // Producto ya guardado en BD (tiene ID asignado)
        productoGuardado = new Productos(1, "Teclado Gamer", 10, 39990, "Electronics");

        // Request que llega desde el cliente (sin ID)
        request = new ProductoRequest();
        request.setNombre("Teclado Gamer");
        request.setCantidad(10);
        request.setPrecio(39990);
        request.setCategoria("Electronics");

        // Categoría válida que retorna la Platzi API
        categoriaValida = new CategoriaDTO();
        categoriaValida.setName("Electronics");
    }

    // =========================================================================
    // guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar: debería guardar el producto y retornar el DTO correctamente")
    void shouldGuardarProductoCorrectamente() {
        // Given: la API externa retorna una categoría válida y el repo guarda el producto
        when(categoriaClient.obtenerCategorias()).thenReturn(List.of(categoriaValida));
        when(productosRepository.save(any(Productos.class))).thenReturn(productoGuardado);

        // When: se llama al método guardar
        ProductoDTO resultado = productosService.guardar(request);

        // Then: el DTO retornado debe tener los datos del producto guardado
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Teclado Gamer", resultado.getNombre());
        assertEquals(10, resultado.getCantidad());
        assertEquals(39990, resultado.getPrecio());
        assertEquals("Electronics", resultado.getCategoria());

        // Verificar que el repositorio fue llamado exactamente una vez
        verify(productosRepository, times(1)).save(any(Productos.class));
    }

    @Test
    @DisplayName("guardar: debería lanzar CategoriaNotFoundException si la categoría no existe")
    void shouldThrowCategoriaNotFoundAlGuardar() {
        // Given: la API externa no retorna la categoría enviada
        CategoriaDTO otraCategoria = new CategoriaDTO();
        otraCategoria.setName("Ropa");
        when(categoriaClient.obtenerCategorias()).thenReturn(List.of(otraCategoria));

        // When + Then: se espera que se lance CategoriaNotFoundException
        assertThrows(CategoriaNotFoundException.class, () -> productosService.guardar(request));

        // El repositorio nunca debe ser llamado si la categoría es inválida
        verify(productosRepository, never()).save(any(Productos.class));
    }

    // =========================================================================
    // listar()
    // =========================================================================

    @Test
    @DisplayName("listar: debería retornar todos los productos como lista de DTOs")
    void shouldListarTodosLosProductos() {
        // Given: el repositorio tiene dos productos
        Productos otroProducto = new Productos(2, "Mouse Gamer", 20, 27990, "Electronics");
        when(productosRepository.findAll()).thenReturn(List.of(productoGuardado, otroProducto));

        // When
        List<ProductoDTO> resultado = productosService.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Teclado Gamer", resultado.get(0).getNombre());
        assertEquals("Mouse Gamer", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("listar: debería retornar lista vacía cuando no hay productos")
    void shouldRetornarListaVaciaAlListar() {
        // Given: el repositorio no tiene productos
        when(productosRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ProductoDTO> resultado = productosService.listar();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("buscarPorId: debería retornar el producto correcto cuando el ID existe")
    void shouldBuscarProductoPorIdCorrectamente() {
        // Given: el repositorio encuentra el producto con id=1
        when(productosRepository.findById(1)).thenReturn(Optional.of(productoGuardado));

        // When
        ProductoDTO resultado = productosService.buscarPorId(1);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Teclado Gamer", resultado.getNombre());
    }

    @Test
    @DisplayName("buscarPorId: debería lanzar ProductoNotFoundException cuando el ID no existe")
    void shouldThrowProductoNotFoundAlBuscarPorId() {
        // Given: el repositorio no encuentra ningún producto con id=99
        when(productosRepository.findById(99)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(ProductoNotFoundException.class, () -> productosService.buscarPorId(99));
    }

    // =========================================================================
    // actualizar()
    // =========================================================================

    @Test
    @DisplayName("actualizar: debería actualizar el producto y retornar el DTO con los nuevos datos")
    void shouldActualizarProductoCorrectamente() {
        // Given
        Productos productoActualizado = new Productos(1, "Teclado Gamer RGB", 15, 45000, "Electronics");
        request.setNombre("Teclado Gamer RGB");
        request.setCantidad(15);
        request.setPrecio(45000);

        when(categoriaClient.obtenerCategorias()).thenReturn(List.of(categoriaValida));
        when(productosRepository.findById(1)).thenReturn(Optional.of(productoGuardado));
        when(productosRepository.save(any(Productos.class))).thenReturn(productoActualizado);

        // When
        ProductoDTO resultado = productosService.actualizar(1, request);

        // Then
        assertNotNull(resultado);
        assertEquals("Teclado Gamer RGB", resultado.getNombre());
        assertEquals(15, resultado.getCantidad());
        assertEquals(45000, resultado.getPrecio());
        verify(productosRepository, times(1)).save(any(Productos.class));
    }

    @Test
    @DisplayName("actualizar: debería lanzar ProductoNotFoundException cuando el ID no existe")
    void shouldThrowProductoNotFoundAlActualizar() {
        // Given: la categoría es válida pero el producto no existe
        when(categoriaClient.obtenerCategorias()).thenReturn(List.of(categoriaValida));
        when(productosRepository.findById(99)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(ProductoNotFoundException.class, () -> productosService.actualizar(99, request));
        verify(productosRepository, never()).save(any(Productos.class));
    }

    // =========================================================================
    // eliminar()
    // =========================================================================

    @Test
    @DisplayName("eliminar: debería eliminar el producto correctamente cuando el ID existe")
    void shouldEliminarProductoCorrectamente() {
        // Given
        when(productosRepository.findById(1)).thenReturn(Optional.of(productoGuardado));
        doNothing().when(productosRepository).deleteById(1);

        // When
        productosService.eliminar(1);

        // Then: verificar que deleteById fue llamado exactamente una vez con el ID correcto
        verify(productosRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminar: debería lanzar ProductoNotFoundException cuando el ID no existe")
    void shouldThrowProductoNotFoundAlEliminar() {
        // Given
        when(productosRepository.findById(99)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(ProductoNotFoundException.class, () -> productosService.eliminar(99));
        verify(productosRepository, never()).deleteById(any());
    }

    // =========================================================================
    // buscarPorNombre()
    // =========================================================================

    @Test
    @DisplayName("buscarPorNombre: debería retornar los productos que coincidan con el nombre")
    void shouldBuscarProductosPorNombre() {
        // Given: el repositorio retorna productos cuyo nombre contiene "teclado" (sin distinguir mayúsculas)
        when(productosRepository.findProductosByNombreContainsIgnoreCase("teclado"))
                .thenReturn(List.of(productoGuardado));

        // When
        List<ProductoDTO> resultado = productosService.buscarPorNombre("teclado");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Teclado Gamer", resultado.get(0).getNombre());
    }
}
