# Proyecto: Gestión de Productos con Spring Boot

Un proyecto educativo diseñado para estudiantes de segundo año de Ingeniería en Computación que enseña los principios fundamentales de **Spring Framework**, decoradores básicos y de validación, buenas prácticas de APIs REST con HTTP status codes, manejo centralizado de errores e integración con base de datos relacional mediante JPA.

Este ejercicio corresponde al ramo **Fullstack I** para estudiantes de **Ingeniería en Informática de DUOC UC**.

## 📚 Objetivos de Aprendizaje

- Comprender la arquitectura de una aplicación Spring Boot
- Dominar decoradores (anotaciones) básicas y de validación
- Implementar APIs REST siguiendo buenas prácticas
- Usar códigos de estado HTTP apropiados
- Implementar manejo centralizado de excepciones con excepciones personalizadas
- Aplicar inyección de dependencias
- Integrar base de datos MySQL mediante Spring Data JPA
- Gestionar el esquema de base de datos mediante migraciones con **Flyway**
- Consumir APIs externas desde el Service mediante **OpenFeign**
- Utilizar el patrón de diseño **CSR (Controller-Service-Repository)**: adaptación de MVC para Spring Boot
- Implementar **logging** con SLF4J y Logback para registrar eventos importantes de la aplicación
- Documentar la API con **Swagger UI** usando SpringDoc OpenAPI para que otros desarrolladores puedan explorar y probar los endpoints
- Escribir **pruebas unitarias** con JUnit 5 y Mockito para verificar el comportamiento del servicio y del controlador de forma aislada

---

## 🔧 Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+** para gestión de dependencias
- **MySQL 8+** con una base de datos llamada `productos`
- **IDE recomendado**: IntelliJ IDEA, Visual Studio Code o Eclipse
- Conocimientos básicos de Java y POO
- Conceptos básicos de APIs REST y HTTP

---

## 📦 Dependencias Principales

```xml
<!-- Spring Boot Starter Web MVC (REST Controllers y servidor web) -->
spring-boot-starter-webmvc

<!-- Spring Boot Starter Validation (Validación de datos) -->
spring-boot-starter-validation

<!-- Spring Boot Starter Data JPA (Persistencia con Hibernate) -->
spring-boot-starter-data-jpa

<!-- MySQL Connector (Driver de base de datos) -->
mysql-connector-j

<!-- Lombok (Generación automática de getters, setters, etc.) -->
lombok

<!-- Flyway Core (Motor de migraciones de base de datos) -->
flyway-core 10.11.1

<!-- Flyway MySQL (Soporte específico para MySQL) -->
flyway-mysql 10.11.1

<!-- Spring Cloud OpenFeign (Consumo declarativo de APIs externas) -->
spring-cloud-starter-openfeign

<!-- SpringDoc OpenAPI UI (Documentación Swagger automática) -->
springdoc-openapi-starter-webmvc-ui 2.6.0

<!-- Spring Boot Starter Test (Pruebas unitarias) -->
spring-boot-starter-test

<!-- H2 Database (Base de datos en memoria para tests — no requiere MySQL) -->
h2
```

---

## ⚙️ Configuración de Base de Datos

### Opción 1: Levantar MySQL con Docker (Recomendado)

Si no tienes MySQL instalado localmente, puedes levantarlo fácilmente con Docker:

```bash
docker run --name some-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql
```

Esto levanta un contenedor MySQL con:
- **Puerto**: 3306 (accesible desde tu máquina)
- **Usuario**: `root`
- **Contraseña**: `my-secret-pw`

Luego crea la base de datos conectándote al contenedor:

```bash
docker exec -it some-mysql mysql -uroot -pmy-secret-pw -e "CREATE DATABASE IF NOT EXISTS productos;"
```

Para detener o reiniciar el contenedor:
```bash
docker stop some-mysql   # Detener
docker start some-mysql  # Reiniciar
```

### Opción 2: MySQL instalado localmente

Crea la base de datos desde cualquier cliente MySQL:
```sql
CREATE DATABASE IF NOT EXISTS productos;
```

---

### Clientes de Base de Datos — Ver tablas y datos

Una vez levantado MySQL, puedes conectarte con cualquiera de estas herramientas para explorar la base de datos y ver las tablas que **Flyway** crea automáticamente al ejecutar las migraciones:

#### 🟠 IntelliJ IDEA (Database Tool Window)
IntelliJ incluye un cliente de base de datos integrado:
1. Ve a **View → Tool Windows → Database**
2. Haz clic en **+** → **Data Source → MySQL**
3. Completa los datos:
   - **Host**: `localhost`
   - **Port**: `3306`
   - **User**: `root`
   - **Password**: `my-secret-pw`
   - **Database**: `productos`
4. Haz clic en **Test Connection** y luego **OK**

Una vez conectado puedes expandir el esquema y ver la tabla `productos` que Flyway crea automáticamente al levantar la aplicación, y también la tabla `flyway_schema_history` que registra las migraciones ejecutadas.

#### 🐬 MySQL Workbench (oficial de Oracle, gratuito)
Descarga: [dev.mysql.com/downloads/workbench](https://dev.mysql.com/downloads/workbench/)

#### 🐘 DBeaver (universal, gratuito)
Soporta MySQL, PostgreSQL y muchos más.
Descarga: [dbeaver.io](https://dbeaver.io/)

#### ⚡ TablePlus (macOS/Windows, versión gratuita disponible)
Descarga: [tableplus.com](https://tableplus.com/)

---

### Configuración en `application.properties`

En `src/main/resources/application.properties`, configura tu conexión MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=my-secret-pw
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 'none' delega el manejo del esquema completamente a Flyway
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# Flyway - Migraciones de base de datos
spring.flyway.enabled=true
spring.flyway.repair=true

# Configuración de logs
logging.level.root=info
logging.level.com.duoc.productos=info
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.file.name=logs/productos.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=7

# Swagger / SpringDoc OpenAPI
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/doc/swagger-ui.html
```

> Con `ddl-auto=none` Hibernate **no toca** el esquema de la base de datos. Es Flyway quien crea y versiona las tablas mediante scripts SQL versionados.

---

## 🏗️ Estructura del Proyecto

```
productos/
├── src/
│   ├── main/
│   │   ├── java/com/duoc/productos/
│   │   │   ├── ProductosApplication.java         # Punto de entrada (@EnableFeignClients)
│   │   │   ├── client/
│   │   │   │   └── CategoriaClient.java          # Cliente Feign para Platzi API
│   │   │   ├── config/
│   │   │   │   └── SwaggerConfig.java            # Configuración de Swagger / OpenAPI
│   │   │   ├── controller/
│   │   │   │   └── ProductosController.java      # Endpoints REST
│   │   │   ├── dto/
│   │   │   │   ├── CategoriaDTO.java             # Respuesta de la API externa
│   │   │   │   ├── ProductoDTO.java              # Respuesta al cliente
│   │   │   │   └── ProductoRequest.java          # Datos de entrada con validaciones
│   │   │   ├── model/
│   │   │   │   └── Productos.java               # Entidad JPA (tabla en BD)
│   │   │   ├── service/
│   │   │   │   └── ProductosService.java         # Lógica de negocio
│   │   │   ├── repository/
│   │   │   │   └── ProductosRepository.java      # Acceso a datos (JPA)
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java   # Manejo centralizado de errores
│   │   │       ├── ProductoNotFoundException.java # Excepción para producto no encontrado
│   │   │       └── CategoriaNotFoundException.java # Excepción para categoría inválida
│   │   └── resources/
│   │       ├── application.properties            # Configuración
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__create_productos_table.sql  # Crea la tabla productos
│   │               ├── V2__create_productos_insert.sql # Datos iniciales de ejemplo
│   │               └── V3__add_column_categoria.sql    # Agrega columna categoria
│   └── test/
│       ├── java/com/duoc/productos/
│       │   ├── ProductosApplicationTests.java        # Verifica que el contexto carga OK
│       │   ├── controller/
│       │   │   └── ProductosControllerTest.java      # 6 pruebas del controlador (MockMvc)
│       │   └── service/
│       │       └── ProductosServiceTest.java         # 11 pruebas del servicio (Mockito)
│       └── resources/
│           └── application.properties               # Config H2 en memoria para tests
└── pom.xml                                           # Configuración Maven
```

---

## 🎯 Conceptos Clave

### 1. **Decoradores (Anotaciones) Básicos de Spring**

Las anotaciones son etiquetas especiales que proporcionan metadatos sobre el programa, no afectan directamente la operación del código pero proveen información al framework.

#### `@SpringBootApplication`
**Ubicación**: [ProductosApplication.java](src/main/java/com/duoc/productos/ProductosApplication.java)

```java
@SpringBootApplication
public class ProductosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductosApplication.class, args);
    }
}
```

**¿Qué hace?** Combina tres anotaciones en una:
- `@Configuration`: Marca la clase como fuente de definiciones de beans
- `@EnableAutoConfiguration`: Permite que Spring Boot configure automáticamente la aplicación basándose en las dependencias
- `@ComponentScan`: Escanea el paquete actual y subpaquetes buscando componentes anotados

---

#### `@RestController`
**Ubicación**: [ProductosController.java](src/main/java/com/duoc/productos/controller/ProductosController.java)

```java
@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController {
    // ...
}
```

**¿Qué hace?**
- Marca la clase como controlador REST
- Equivalente a `@Controller` + `@ResponseBody`
- Los métodos retornan datos serializados (JSON) automáticamente
- `@RequestMapping("/api/v1/productos")`: Define la ruta base para todos los endpoints

---

#### `@Autowired`
**Ubicación**: [ProductosController.java](src/main/java/com/duoc/productos/controller/ProductosController.java)

```java
@Autowired
private ProductosService productosService;
```

**¿Qué hace?**
- **Inyección de Dependencias**: Spring inyecta automáticamente una instancia de `ProductosService`
- No necesitas crear la instancia manualmente con `new`
- Spring gestiona el ciclo de vida del objeto
- Promueve el desacoplamiento y facilita las pruebas

---

#### `@Service`
**Ubicación**: [ProductosService.java](src/main/java/com/duoc/productos/service/ProductosService.java)

```java
@Service
public class ProductosService {
    // ...
}
```

**¿Qué hace?**
- Marca la clase como un bean de servicio (componente del negocio)
- Contiene la lógica empresarial
- Spring la detecta automáticamente y la registra como bean

---

#### `@Repository`
**Ubicación**: [ProductosRepository.java](src/main/java/com/duoc/productos/repository/ProductosRepository.java)

```java
@Repository
public interface ProductosRepository extends JpaRepository<Productos, Integer> {
    List<Productos> findProductosByNombreContainsIgnoreCase(String nombre);
}
```

**¿Qué hace?**
- Marca la interfaz como repositorio (capa de acceso a datos)
- Al extender `JpaRepository`, hereda operaciones CRUD completas automáticamente
- Spring genera la implementación en tiempo de ejecución
- `findProductosByNombreContainsIgnoreCase`: Spring deriva la consulta SQL del nombre del método

---

#### `@Entity` y `@Table`
**Ubicación**: [Productos.java](src/main/java/com/duoc/productos/model/Productos.java)

```java
@Entity
@Table(name = "productos")
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // ...
}
```

**¿Qué hacen?**
- `@Entity`: Marca la clase como entidad JPA (mapeada a una tabla en la BD)
- `@Table(name = "productos")`: Especifica el nombre de la tabla
- `@Id`: Marca el campo como llave primaria
- `@GeneratedValue`: El valor se genera automáticamente (auto-increment)

---

### 2. **Patrón DTO (Data Transfer Object)**

Este proyecto separa la entidad de base de datos del objeto que se expone al cliente, usando dos clases:

#### `ProductoRequest` — Datos de entrada
**Ubicación**: [ProductoRequest.java](src/main/java/com/duoc/productos/dto/ProductoRequest.java)

```java
@Data
public class ProductoRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Integer precio;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;
}
```

#### `ProductoDTO` — Datos de salida
**Ubicación**: [ProductoDTO.java](src/main/java/com/duoc/productos/dto/ProductoDTO.java)

```java
@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private Integer cantidad;
    private Integer precio;
    private String categoria;
}
```

**¿Por qué separar?**
- El cliente no debería enviar el `id` al crear (lo genera la BD)
- Permite controlar qué campos se exponen en la respuesta
- Protege la entidad interna de cambios externos

---

### 3. **Decoradores de Validación**

Garantizan que los datos recibidos cumplan con reglas específicas **antes** de procesarlos.

#### `@NotNull`
- **Valida**: El valor no puede ser `null`
- **Tipo**: Cualquier tipo de datos

#### `@NotBlank`
- **Valida**: El string no puede ser `null` ni estar vacío
- **Trim**: Considera espacios en blanco como vacío
- **Tipo**: Solo strings

#### `@Positive`
- **Valida**: El número debe ser estrictamente mayor que 0
- **Tipo**: Números (Integer, Double, BigDecimal, etc.)

#### Decoradores de Lombok

```java
@Data                    // Genera: getters, setters, equals(), hashCode(), toString()
@NoArgsConstructor       // Genera constructor sin argumentos
@AllArgsConstructor      // Genera constructor con todos los campos
```

---

### 4. **Mapeo de Endpoints REST**

**Ubicación**: [ProductosController.java](src/main/java/com/duoc/productos/controller/ProductosController.java)

```java
@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController {

    // POST - Crear producto
    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request)

    // GET - Listar todos o filtrar por nombre
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(@RequestParam(required = false) String nombre)

    // GET - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Integer id)

    // PUT - Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoRequest request)

    // DELETE - Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id)
}
```

| Decorator | Verbo HTTP | Operación | Descripción |
|-----------|-----------|-----------|-------------|
| `@GetMapping` | GET | Read | Obtiene datos sin modificarlos |
| `@PostMapping` | POST | Create | Crea un nuevo recurso |
| `@PutMapping` | PUT | Update | Actualiza completamente un recurso |
| `@DeleteMapping` | DELETE | Delete | Elimina un recurso |

#### Parámetros Importantes

```java
// @RequestBody: Mapea el JSON del request al objeto
// @Valid: Activa la validación de datos
@PostMapping
public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request) { }

// @PathVariable: Extrae valores de la ruta URL
@GetMapping("/{id}")
public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Integer id) { }

// @RequestParam: Extrae parámetros de la query string (?nombre=xyz)
@GetMapping
public ResponseEntity<List<ProductoDTO>> listar(@RequestParam(required = false) String nombre) { }
```

---

### 5. **HTTP Status Codes (Códigos de Estado)**

| Código | Nombre | Significado | Cuándo se usa en este proyecto |
|--------|--------|------------|-------------------------------|
| **200** | OK | Solicitud exitosa | GET por ID, PUT exitoso |
| **201** | Created | Recurso creado | POST exitoso |
| **204** | No Content | Sin contenido | GET con lista vacía, DELETE exitoso |
| **400** | Bad Request | Datos inválidos | Validaciones fallidas |
| **404** | Not Found | Recurso no encontrado | ID inexistente en GET, PUT, DELETE — categoría inválida en POST, PUT |

```java
// 201 - Crear producto
return new ResponseEntity<>(productosService.guardar(request), HttpStatus.CREATED);

// 200 - Buscar por ID
return new ResponseEntity<>(productosService.buscarPorId(id), HttpStatus.OK);

// 204 - Lista vacía
if (productos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

// 404 - Manejado automáticamente por GlobalExceptionHandler via ProductoNotFoundException
```

---

### 6. **Excepción Personalizada y Manejo Centralizado de Errores**

En lugar de manejar errores con try/catch en cada endpoint, Spring permite centralizar la gestión.

#### `ProductoNotFoundException`
**Ubicación**: [ProductoNotFoundException.java](src/main/java/com/duoc/productos/exception/ProductoNotFoundException.java)

```java
public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(Integer id) {
        super("Producto no encontrado con id: " + id);
    }
}
```

**¿Qué hace?**
- Extiende `RuntimeException` (no necesita declararse con `throws`)
- Se lanza desde el `Service` cuando no se encuentra un producto
- El mensaje incluye el ID buscado para mayor claridad

#### `GlobalExceptionHandler`
**Ubicación**: [GlobalExceptionHandler.java](src/main/java/com/duoc/productos/exception/GlobalExceptionHandler.java)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errores.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ProductoNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

**¿Cómo funciona el flujo?**

1. Cliente solicita `GET /api/v1/productos/999` (no existe)
2. `ProductosService` lanza `ProductoNotFoundException(999)`
3. `GlobalExceptionHandler` la intercepta automáticamente
4. Retorna JSON `{"error": "Producto no encontrado con id: 999"}` con status 404

#### `CategoriaNotFoundException`
**Ubicación**: [CategoriaNotFoundException.java](src/main/java/com/duoc/productos/exception/CategoriaNotFoundException.java)

```java
public class CategoriaNotFoundException extends RuntimeException {
    public CategoriaNotFoundException(String nombre) {
        super("Categoria no encontrada con nombre: " + nombre);
    }
}
```

**¿Qué hace?**
- Se lanza desde `ProductosService` cuando la categoría enviada no existe en la Platzi API
- El mensaje incluye el nombre de la categoría buscada para mayor claridad

**Handler en `GlobalExceptionHandler`:**

```java
@ExceptionHandler(CategoriaNotFoundException.class)
public ResponseEntity<Map<String, String>> handleCategoriaNotFound(CategoriaNotFoundException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
```

**¿Cómo funciona el flujo?**

1. Cliente envía `POST /api/v1/productos` con `"categoria": "Deportes"` (no existe en Platzi)
2. `ProductosService` llama a `CategoriaClient` → obtiene la lista de categorías válidas
3. `"Deportes"` no está en la lista → lanza `CategoriaNotFoundException("Deportes")`
4. `GlobalExceptionHandler` la intercepta automáticamente
5. Retorna JSON `{"error": "Categoria no encontrada con nombre: Deportes"}` con status 404

---

### 7. **Migraciones de Base de Datos con Flyway**

#### ¿Por qué usar un sistema de migraciones?

Cuando trabajas con `spring.jpa.hibernate.ddl-auto=update`, Hibernate intenta modificar el esquema de la base de datos automáticamente en cada arranque. Esto es conveniente durante el desarrollo inicial, pero presenta problemas reales en proyectos colaborativos y en producción:

| Problema con `ddl-auto=update` | Solución con Flyway |
|-------------------------------|---------------------|
| No hay registro de qué cambios se aplicaron | Historial completo en `flyway_schema_history` |
| Dos developers pueden tener esquemas distintos | Todos aplican exactamente los mismos scripts en el mismo orden |
| No se puede replicar el estado exacto de producción | Las migraciones son reproducibles y versionadas |
| Hibernate no elimina columnas (solo agrega) | Control total sobre ALTER TABLE, DROP, etc. |
| Sin datos iniciales de forma controlada | Scripts de seed como parte del historial de migraciones |

> **Regla de oro**: En proyectos reales, el esquema de base de datos es código. Debe versionarse, revisarse y aplicarse de forma controlada, igual que el código fuente.

---

#### ¿Cómo funciona Flyway?

Al arrancar la aplicación, Flyway:
1. Busca scripts SQL en `src/main/resources/db/migration/`
2. Revisa la tabla `flyway_schema_history` (la crea si no existe) para saber qué migraciones ya se aplicaron
3. Ejecuta **en orden** solo las migraciones pendientes
4. Registra cada migración ejecutada con su versión, descripción y checksum

```
flyway_schema_history
┌─────────┬─────────────┬─────────────────────────────┬──────────┬────────────┐
│ version │ description │ script                      │ checksum │ success    │
├─────────┼─────────────┼─────────────────────────────┼──────────┼────────────┤
│ 1       │ create...   │ V1__create_productos_table.. │ 12345678 │ true       │
│ 2       │ create...   │ V2__create_productos_insert. │ 87654321 │ true       │
└─────────┴─────────────┴─────────────────────────────┴──────────┴────────────┘
```

---

#### Convención de nombres (obligatoria)

```
V{versión}__{descripción}.sql
```

- `V` — prefijo obligatorio (mayúscula)
- `{versión}` — número de versión (1, 2, 3... o 1.1, 1.2...)
- `__` — **doble guión bajo** (separador obligatorio)
- `{descripción}` — texto descriptivo (guiones bajos como espacios)

```
✅ V1__create_productos_table.sql
✅ V2__create_productos_insert.sql
✅ V3__add_column_categoria.sql
❌ V1_create_productos_table.sql   (un solo guión bajo — Flyway lo ignora)
❌ v1__create_table.sql            (v minúscula — no se detecta)
```

---

#### Scripts de migración en este proyecto

**`V1__create_productos_table.sql`** — Crea la tabla principal:
```sql
CREATE TABLE `productos` (
  `id`       int NOT NULL AUTO_INCREMENT,
  `cantidad` int DEFAULT NULL,
  `nombre`   varchar(255) NOT NULL,
  `precio`   int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**`V2__create_productos_insert.sql`** — Inserta datos iniciales de ejemplo:
```sql
INSERT INTO productos.productos (cantidad, nombre, precio) VALUES (10, 'Teclado Gamer Razer', 39990);
INSERT INTO productos.productos (cantidad, nombre, precio) VALUES (20, 'Mouse Gamer Razer', 27990);
```

**`V3__add_column_categoria.sql`** — Agrega la columna `categoria` a la tabla:
```sql
ALTER TABLE productos ADD COLUMN categoria VARCHAR(100) NOT NULL DEFAULT 'Sin categoría';
```

> Esta migración añade el campo `categoria` que se valida contra la Platzi Fake Store API. El valor `DEFAULT 'Sin categoría'` permite que los registros existentes tengan un valor válido al agregar la columna.

---

#### Configuración en `application.properties`

```properties
# Delega el esquema completamente a Flyway (no tocar con Hibernate)
spring.jpa.hibernate.ddl-auto=none

# Flyway
spring.flyway.enabled=true
spring.flyway.repair=true  # Repara migraciones fallidas (útil en desarrollo)
```

> `spring.flyway.repair=true` permite que Flyway repare entradas fallidas en `flyway_schema_history`. Útil durante el desarrollo, pero debe evaluarse en producción.

---

### 8. **Consumo de APIs Externas con OpenFeign**

#### ¿Por qué consumir una API externa?

En aplicaciones reales, no toda la información vive en tu propia base de datos. A veces necesitas **delegar responsabilidades a servicios externos**: validar datos, obtener información de terceros, o comunicarte con otros microservicios.

En este proyecto, en lugar de mantener una lista de categorías hardcodeada en el código, consultamos la **Platzi Fake Store API** para obtener las categorías válidas en tiempo real. Así, al crear o actualizar un producto, validamos que la categoría enviada sea real:

```
POST /api/v1/productos  →  ProductosService  →  CategoriaClient  →  api.escuelajs.co
                                                     ↓
                                          ["Ropa", "Electrónica", "Muebles"...]
                                                     ↓
                                        ¿"Electrónica" está en la lista? ✅ → guardar
                                        ¿"Xyz" está en la lista?         ❌ → error 404
```

---

#### Repository vs FeignClient — son lo mismo para el Service

Este es uno de los conceptos más importantes de este ejercicio. Observa cómo el `ProductosService` usa **dos dependencias inyectadas**:

```java
@Autowired
private ProductosRepository productosRepository;  // Accede a MySQL

@Autowired
private CategoriaClient categoriaClient;          // Accede a Platzi API
```

Para el Service, **ambos son exactamente lo mismo**: reciben una llamada y devuelven objetos Java. La diferencia es solo de dónde vienen los datos:

| | `ProductosRepository` | `CategoriaClient` |
|---|---|---|
| **¿Qué es?** | Interfaz JPA | Interfaz Feign |
| **¿De dónde trae datos?** | Base de datos MySQL | API externa (internet) |
| **¿Qué devuelve?** | `Productos`, `List<Productos>` | `List<CategoriaDTO>` |
| **¿Cómo se inyecta?** | `@Autowired` | `@Autowired` |
| **¿Quién genera la implementación?** | Spring Data JPA | Spring Cloud OpenFeign |

> El Service **no sabe** (ni le importa) si los datos vienen de una base de datos o de internet. Solo sabe que tiene una dependencia que le devuelve lo que necesita. Esto es la **inyección de dependencias** llevada a su máxima expresión.

---

#### Configuración necesaria

**1. Habilitar Feign en el punto de entrada:**

```java
@SpringBootApplication
@EnableFeignClients               // Activa el escaneo de interfaces @FeignClient
public class ProductosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductosApplication.class, args);
    }
}
```

**2. Declarar el cliente con `@FeignClient`:**
**Ubicación**: [CategoriaClient.java](src/main/java/com/duoc/productos/client/CategoriaClient.java)

```java
@FeignClient(name = "platzi-store", url = "${platzi.api.url:https://api.escuelajs.co/api/v1}")
public interface CategoriaClient {

    @GetMapping("/categories")
    List<CategoriaDTO> obtenerCategorias();
}
```

**¿Qué hace `@FeignClient`?**
- Marca la interfaz como cliente HTTP declarativo
- `name`: identificador interno del cliente
- `url`: dirección base de la API (se puede configurar en `application.properties`)
- Spring genera automáticamente la implementación en tiempo de ejecución — **no escribes ningún código HTTP**

**3. DTO para mapear la respuesta de la API:**
**Ubicación**: [CategoriaDTO.java](src/main/java/com/duoc/productos/dto/CategoriaDTO.java)

```java
@Data
public class CategoriaDTO {
    private Integer id;
    private String name;
}
```

Feign deserializa automáticamente el JSON de la API al objeto Java:
```json
[
  { "id": 1, "name": "Ropa" },
  { "id": 2, "name": "Electrónica" },
  { "id": 3, "name": "Muebles" }
]
```

**4. Uso en el Service:**

```java
private void validarCategoria(String categoria) {
    List<CategoriaDTO> categorias = categoriaClient.obtenerCategorias(); // llama a la API
    boolean existe = categorias.stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(categoria));
    if (!existe) {
        throw new CategoriaNotFoundException(categoria);
    }
}
```

**5. URL configurable en `application.properties`:**

```properties
platzi.api.url=https://api.escuelajs.co/api/v1
```

---

### 9. **Logging con SLF4J y `@Slf4j`**

#### ¿Qué es el logging y por qué es importante?

El **logging** (registro de eventos) es la práctica de anotar lo que está ocurriendo dentro de la aplicación mientras se ejecuta. En lugar de usar `System.out.println()`, usamos un framework de logging que permite:

- Controlar el **nivel de detalle** de los mensajes
- Escribir los mensajes tanto en la **consola** como en un **archivo**
- **Rotar archivos** automáticamente cuando se llenan
- Apagar o encender mensajes según el entorno (desarrollo, producción)

> Imagina que tu aplicación en producción falla a las 3 AM. Si no tienes logs, ¿cómo sabrás qué pasó? Los logs son como la caja negra de un avión: registran todo para que puedas investigar después.

---

#### La anotación `@Slf4j` de Lombok

En lugar de crear manualmente el objeto de logging, Lombok lo genera automáticamente con la anotación `@Slf4j`:

```java
// Sin Lombok (verboso, repetitivo)
private static final Logger log = LoggerFactory.getLogger(ProductosController.class);

// Con Lombok @Slf4j (equivalente, pero automático)
@Slf4j
@RestController
public class ProductosController {
    // Ya tienes disponible la variable "log" sin escribir nada más
}
```

**¿Qué hace `@Slf4j`?**
- Genera automáticamente una variable `log` de tipo `org.slf4j.Logger`
- El nombre de la clase se usa como identificador del logger (útil para filtrar logs)
- Es una de las integraciones de Lombok más utilizadas en proyectos Spring Boot

---

#### Niveles de Log

Los mensajes de log tienen diferentes niveles de importancia, de menor a mayor:

| Nivel | Método | ¿Cuándo usarlo? |
|-------|--------|-----------------|
| `TRACE` | `log.trace()` | Información muy detallada (flujo interno paso a paso) |
| `DEBUG` | `log.debug()` | Información útil para depurar durante desarrollo |
| `INFO` | `log.info()` | Eventos normales de la aplicación (algo sucedió correctamente) |
| `WARN` | `log.warn()` | Algo inesperado ocurrió, pero la aplicación sigue funcionando |
| `ERROR` | `log.error()` | Un error ocurrió y debe investigarse |

> Con `logging.level.root=info` en `application.properties`, solo verás los niveles `INFO`, `WARN` y `ERROR`. Los niveles `DEBUG` y `TRACE` quedan silenciados.

---

#### Uso en este proyecto

**En `ProductosController`** — Registrar cada request que llega:

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController {

    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request) {
        log.info("El request para crear un producto fue: " + request);
        return new ResponseEntity<>(productosService.guardar(request), HttpStatus.CREATED);
    }
}
```

**En `ProductosService`** — Confirmar que la operación fue exitosa:

```java
@Slf4j
@Service
public class ProductosService {

    public ProductoDTO guardar(ProductoRequest request) {
        // ... lógica de negocio ...
        log.info("Producto almacenado correctamente: " + producto);
        return convertirADTO(productosRepository.save(producto));
    }
}
```

**En `GlobalExceptionHandler`** — Registrar los errores que ocurren:

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(...) {
        // ...
        log.error("Error: {}", errores);          // Error de validación de datos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ProductoNotFoundException ex) {
        // ...
        log.error("Error: {}", error);            // Producto no encontrado
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCategoriaNotFound(CategoriaNotFoundException ex) {
        // ...
        log.warn("Validation error: {}", error);  // Categoría no válida (advertencia)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

> **¿Por qué `log.warn()` para la categoría y `log.error()` para los demás?**
> Porque una categoría inválida puede ser simplemente un error del usuario (envió mal el dato), mientras que un producto no encontrado o datos de validación fallidos pueden indicar un problema más grave en la integración.

---

#### Configuración de logging en `application.properties`

```properties
# Nivel global para toda la aplicación (INFO y superiores aparecen en los logs)
logging.level.root=info

# Nivel específico para las clases de este proyecto
# Permite cambiar solo el nivel de tus clases sin afectar las de Spring o librerías
logging.level.com.duoc.productos=info

# Formato de los mensajes en la consola
# %d{...}   → fecha y hora
# [%thread] → nombre del hilo de ejecución
# %-5level  → nivel del log alineado a 5 caracteres (INFO , WARN , ERROR)
# %logger{36} → nombre de la clase que generó el log (máx. 36 chars)
# %msg%n    → el mensaje + salto de línea
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Formato más simple para el archivo (sin nivel ni hilo, para que sea más legible)
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Ruta y nombre del archivo donde se guardan los logs
# Se crea automáticamente en la carpeta "logs/" dentro del proyecto
logging.file.name=logs/productos.log

# Tamaño máximo de cada archivo de log antes de rotar (crear uno nuevo)
logging.logback.rollingpolicy.max-file-size=10MB

# Cantidad máxima de archivos de log históricos a conservar (7 días de historial)
logging.logback.rollingpolicy.max-history=7
```

#### Rotación de archivos de log

Cuando el archivo `productos.log` llega a 10 MB, Spring Boot lo archiva automáticamente y crea uno nuevo. Con `max-history=7`, se conservan los últimos 7 archivos, eliminando los más antiguos.

```
logs/
├── productos.log            ← archivo activo (el que se está escribiendo)
├── productos.log.2026-06-01.gz  ← archivos comprimidos anteriores
├── productos.log.2026-06-02.gz
└── ...
```

> La carpeta `logs/` está agregada en `.gitignore` para que no se suba al repositorio. Los logs son datos de ejecución, no código fuente.

---

#### ¿Cómo se ve un log en la consola?

Cuando creas un producto, verás algo así en la consola:

```
2026-06-06 10:35:22 [http-nio-8080-exec-1] INFO  c.d.p.controller.ProductosController - El request para crear un producto fue: ProductoRequest(nombre=Leche, cantidad=10, precio=1500, categoria=Electronics)
2026-06-06 10:35:22 [http-nio-8080-exec-1] INFO  c.d.p.service.ProductosService - Producto almacenado correctamente: Productos(id=1, nombre=Leche, ...)
```

Si hay un error de validación:
```
2026-06-06 10:35:40 [http-nio-8080-exec-2] ERROR c.d.p.exception.GlobalExceptionHandler - Error: {nombre=El nombre no puede estar vacío}
```

---

### 10. **Documentación de API con Swagger / SpringDoc OpenAPI**

#### ¿Qué es Swagger y por qué es importante?

Cuando construyes una API REST, otras personas necesitan saber cómo usarla: qué endpoints existen, qué datos enviar, qué respuestas esperar. Sin documentación, cada desarrollador tendría que leer el código fuente para entender la API.

**Swagger UI** es una interfaz web interactiva que genera esta documentación **automáticamente** a partir del código. Se actualiza sola cada vez que agregas o modificas endpoints.

> Imagina que tu API es un cajero automático. Swagger UI es el manual de instrucciones con pantallas reales donde puedes practicar cada operación sin riesgo de romper nada.

**OpenAPI** es el estándar (especificación) que define cómo se debe describir una API REST. Swagger UI es la herramienta que visualiza ese estándar. En este proyecto usamos **SpringDoc**, la librería que integra OpenAPI con Spring Boot automáticamente.

---

#### Paso 1: Agregar la dependencia en `pom.xml`

```xml
<!-- Swagger / SpringDoc OpenAPI UI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

SpringDoc escanea todos los `@RestController` y genera la documentación sin que escribas nada más. Solo con esta dependencia, Swagger ya funciona.

---

#### Paso 2: Configurar en `application.properties`

```properties
# Activa la generación del JSON con la especificación OpenAPI
springdoc.api-docs.enabled=true

# Activa la interfaz visual de Swagger UI
springdoc.swagger-ui.enabled=true

# Ruta personalizada donde se sirve Swagger UI
springdoc.swagger-ui.path=/doc/swagger-ui.html
```

Una vez levantada la aplicación, accede a:
**`http://localhost:8080/doc/swagger-ui/index.html`**

---

#### Paso 3: Clase de configuración `SwaggerConfig`

**Ubicación**: [SwaggerConfig.java](src/main/java/com/duoc/productos/config/SwaggerConfig.java)

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Productos")
                        .description("API REST para la gestión de productos...")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("DUOC UC - Fullstack I")
                                .url("https://www.duoc.cl")));
    }
}
```

**¿Qué hace?**
- `@Configuration`: Spring la detecta al iniciar y ejecuta los `@Bean` que contiene
- `@Bean`: Registra el objeto `OpenAPI` en el contexto de Spring
- `Info`: Personaliza el encabezado que aparece en Swagger UI (título, descripción, versión, contacto)

Sin esta clase, Swagger funciona igual pero muestra un título genérico. Esta clase le da identidad a tu API.

---

#### Paso 4: Documentar el Controller con anotaciones Swagger

**Ubicación**: [ProductosController.java](src/main/java/com/duoc/productos/controller/ProductosController.java)

##### `@Tag` — Agrupa los endpoints bajo un nombre

```java
@Tag(name = "Productos", description = "Operaciones relacionadas con la gestión de productos")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController { }
```

Aparece en Swagger UI como una sección colapsable con todos los endpoints del controlador.

---

##### `@Operation` — Describe un endpoint específico

```java
@Operation(
    summary = "Crear un producto",
    description = "Crea un nuevo producto. La categoría es validada contra la Platzi Fake Store API."
)
@PostMapping
public ResponseEntity<ProductoDTO> guardar(...) { }
```

- `summary`: Título corto que aparece al lado del verbo HTTP
- `description`: Texto explicativo que aparece al expandir el endpoint

---

##### `@ApiResponses` y `@ApiResponse` — Documentan las posibles respuestas

```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoDTO.class))),
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content),
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
            content = @Content)
})
```

- `responseCode`: Código HTTP de la respuesta
- `description`: Explicación de cuándo ocurre
- `content + schema`: Le dice a Swagger qué tipo de objeto devuelve (para generar el ejemplo)
- `content = @Content` vacío: indica que esa respuesta no tiene cuerpo

---

##### `@Parameter` — Describe parámetros de ruta y query

```java
@GetMapping("/{id}")
public ResponseEntity<ProductoDTO> buscarPorId(
        @Parameter(description = "ID del producto a buscar", required = true)
        @PathVariable Integer id) { }

@GetMapping
public ResponseEntity<List<ProductoDTO>> listar(
        @Parameter(description = "Nombre del producto para filtrar (opcional)")
        @RequestParam(required = false) String nombre) { }
```

Swagger muestra estos parámetros con descripción y permite probarlos directamente desde el navegador.

---

#### Paso 5: Documentar los DTOs con `@Schema`

**Ubicación**: [ProductoRequest.java](src/main/java/com/duoc/productos/dto/ProductoRequest.java) y [ProductoDTO.java](src/main/java/com/duoc/productos/dto/ProductoDTO.java)

```java
@Schema(description = "Datos requeridos para crear o actualizar un producto")
public class ProductoRequest {

    @Schema(description = "Nombre del producto", example = "Teclado Gamer Razer")
    private String nombre;

    @Schema(description = "Cantidad disponible en inventario", example = "10")
    private Integer cantidad;

    @Schema(description = "Precio del producto en pesos chilenos", example = "39990")
    private Integer precio;

    @Schema(description = "Categoría del producto (debe existir en Platzi Fake Store API)", example = "Electronics")
    private String categoria;
}
```

**¿Qué aporta `@Schema`?**
- Swagger genera automáticamente un JSON de ejemplo con los valores del campo `example`
- Cualquier desarrollador que use la API sabe exactamente qué formato enviar
- El botón "Try it out" en Swagger UI aparece pre-llenado con los ejemplos

---

#### ¿Cómo se ve Swagger UI?

Al acceder a `http://localhost:8080/doc/swagger-ui/index.html` verás:

```
┌─────────────────────────────────────────────────────┐
│  API Gestión de Productos  v1.0.0                    │
│  API REST para la gestión de productos...            │
├─────────────────────────────────────────────────────┤
│  ▼ Productos  Operaciones relacionadas con...        │
│  ┌──────────────────────────────────────────────┐   │
│  │ POST  /api/v1/productos   Crear un producto  │   │
│  │ GET   /api/v1/productos   Listar productos   │   │
│  │ GET   /api/v1/productos/{id}  Buscar por ID  │   │
│  │ PUT   /api/v1/productos/{id}  Actualizar     │   │
│  │ DELETE /api/v1/productos/{id} Eliminar       │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

Cada endpoint es expandible y tiene un botón **"Try it out"** que permite ejecutar peticiones reales directamente desde el navegador, sin necesidad de Postman ni cURL.

---

### 11. **Pruebas Unitarias con JUnit 5 y Mockito**

#### ¿Por qué es importante tener pruebas unitarias?

Cuando modificas código en un proyecto sin pruebas, no tienes forma de saber si rompiste algo que antes funcionaba. Las **pruebas unitarias** son porciones de código que verifican automáticamente que tu código hace lo que se supone que debe hacer.

> Imagina que construyes un edificio y cada piso tiene sensores que te avisan si algo se rompe. Las pruebas unitarias son esos sensores: detectan problemas automáticamente antes de que lleguen a producción.

**Beneficios:**
- 🔍 Detectan errores en etapas tempranas del desarrollo
- 🔄 Permiten refactorizar con confianza (si los tests siguen pasando, nada se rompió)
- 📄 Actúan como documentación viva del comportamiento esperado
- 🚀 Son esenciales para CI/CD (integración y entrega continua)

---

#### Herramientas utilizadas

| Herramienta | ¿Qué es? | ¿Para qué se usa? |
|-------------|----------|-------------------|
| **JUnit 5** | Framework de pruebas para Java | Escribir y ejecutar los tests (`@Test`, `@BeforeEach`, `@DisplayName`) |
| **Mockito** | Framework de simulación (mocking) | Reemplazar dependencias reales por objetos simulados |
| **MockMvc** | Utilidad de Spring Test | Simular peticiones HTTP al controller sin levantar un servidor real |
| **H2** | Base de datos en memoria | Reemplaza MySQL en tests de integración — no requiere servidor externo |

---

#### Estructura de una prueba: Given - When - Then (AAA)

Todo buen test sigue la estructura **AAA** (Arrange, Act, Assert), también llamada **Given - When - Then**:

```java
@Test
@DisplayName("guardar: debería guardar el producto y retornar el DTO correctamente")
void shouldGuardarProductoCorrectamente() {

    // Given (Arrange) — preparar el escenario: configurar los mocks
    when(categoriaClient.obtenerCategorias()).thenReturn(List.of(categoriaValida));
    when(productosRepository.save(any(Productos.class))).thenReturn(productoGuardado);

    // When (Act) — ejecutar el método que estamos probando
    ProductoDTO resultado = productosService.guardar(request);

    // Then (Assert) — verificar que el resultado es el esperado
    assertNotNull(resultado);
    assertEquals("Teclado Gamer", resultado.getNombre());
    verify(productosRepository, times(1)).save(any(Productos.class));
}
```

---

#### Configuración de base de datos para tests

El proyecto usa **perfiles de Spring** para separar la base de datos de desarrollo de la de pruebas. Cuando los tests se ejecutan, Spring usa automáticamente `src/test/resources/application.properties` con H2 en lugar de MySQL:

```properties
# src/test/resources/application.properties

# H2 en memoria: se crea al iniciar el test y se destruye al terminar
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate crea el esquema automáticamente (no usa Flyway)
spring.jpa.hibernate.ddl-auto=create-drop

# Flyway deshabilitado — sus scripts usan sintaxis MySQL incompatible con H2
spring.flyway.enabled=false
```

> Esto garantiza que los tests **nunca modifican la base de datos de desarrollo**. Cada ejecución parte de cero con una BD limpia en memoria.

---

#### Pruebas del servicio: `ProductosServiceTest`

**Ubicación**: [ProductosServiceTest.java](src/test/java/com/duoc/productos/service/ProductosServiceTest.java)

Usa `@ExtendWith(MockitoExtension.class)` — **no levanta el contexto de Spring**. Es la forma más rápida y aislada de probar lógica de negocio.

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ProductosService")
class ProductosServiceTest {

    @Mock
    private ProductosRepository productosRepository; // Simula la BD

    @Mock
    private CategoriaClient categoriaClient;         // Simula la API externa

    @InjectMocks
    private ProductosService productosService;       // La clase real que probamos
}
```

**¿Qué hace cada anotación?**
- `@Mock`: Crea un objeto simulado. Sus métodos no hacen nada real hasta que los configuras con `when(...)`
- `@InjectMocks`: Crea una instancia real de `ProductosService` e inyecta los mocks como dependencias
- `@ExtendWith(MockitoExtension.class)`: Activa el procesamiento de `@Mock` e `@InjectMocks` automáticamente

**Casos de prueba implementados (11 tests):**

| Test | Método | Escenario |
|------|--------|-----------|
| `shouldGuardarProductoCorrectamente` | `guardar()` | Categoría válida → retorna DTO |
| `shouldThrowCategoriaNotFoundAlGuardar` | `guardar()` | Categoría inválida → lanza excepción |
| `shouldListarTodosLosProductos` | `listar()` | BD con datos → retorna lista |
| `shouldRetornarListaVaciaAlListar` | `listar()` | BD vacía → retorna lista vacía |
| `shouldBuscarProductoPorIdCorrectamente` | `buscarPorId()` | ID existe → retorna producto |
| `shouldThrowProductoNotFoundAlBuscarPorId` | `buscarPorId()` | ID no existe → lanza excepción |
| `shouldActualizarProductoCorrectamente` | `actualizar()` | ID existe → retorna DTO actualizado |
| `shouldThrowProductoNotFoundAlActualizar` | `actualizar()` | ID no existe → lanza excepción |
| `shouldEliminarProductoCorrectamente` | `eliminar()` | ID existe → llama a `deleteById` |
| `shouldThrowProductoNotFoundAlEliminar` | `eliminar()` | ID no existe → lanza excepción |
| `shouldBuscarProductosPorNombre` | `buscarPorNombre()` | Nombre parcial → retorna coincidencias |

---

#### Pruebas del controller: `ProductosControllerTest`

**Ubicación**: [ProductosControllerTest.java](src/test/java/com/duoc/productos/controller/ProductosControllerTest.java)

Usa `@WebMvcTest` — carga solo la capa web (controller + `GlobalExceptionHandler`). **No conecta a la BD**. Ideal para probar rutas, validaciones y códigos de estado HTTP.

```java
@WebMvcTest(ProductosController.class)
@DisplayName("Pruebas unitarias - ProductosController")
class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP

    @MockitoBean
    private ProductosService productosService; // Reemplaza el servicio real
}
```

**¿Qué hace cada anotación?**
- `@WebMvcTest(ProductosController.class)`: Carga únicamente el controller especificado y la capa web
- `@MockitoBean`: Registra un mock de `ProductosService` en el contexto de Spring *(en Spring Boot 4.x reemplaza al antiguo `@MockBean`)*
- `MockMvc`: Permite hacer peticiones HTTP simuladas y verificar la respuesta completa

**¿Cómo funciona MockMvc?**

```java
mockMvc.perform(post("/api/v1/productos")          // simula la petición
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestValido)))
    .andExpect(status().isCreated())               // verifica el status code 201
    .andExpect(jsonPath("$.nombre").value("Teclado Gamer")); // verifica el JSON
```

**Casos de prueba implementados (6 tests):**

| Test | Endpoint | Escenario | Status esperado |
|------|----------|-----------|-----------------|
| `shouldGuardarProductoYRetornar201` | `POST /api/v1/productos` | Datos válidos → crea producto | 201 |
| `shouldRetornar400CuandoDatosInvalidos` | `POST /api/v1/productos` | Datos inválidos → falla validación | 400 |
| `shouldListarProductosYRetornar200` | `GET /api/v1/productos` | Hay productos → retorna lista | 200 |
| `shouldRetornar204CuandoListaVacia` | `GET /api/v1/productos` | Sin productos → sin contenido | 204 |
| `shouldBuscarPorIdYRetornar200` | `GET /api/v1/productos/{id}` | ID existe → retorna producto | 200 |
| `shouldRetornar404CuandoIdNoExiste` | `GET /api/v1/productos/{id}` | ID no existe → error | 404 |

---

#### Cómo ejecutar los tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar solo los tests del servicio
mvn test -Dtest=ProductosServiceTest

# Ejecutar solo los tests del controlador
mvn test -Dtest=ProductosControllerTest
```

**Salida esperada:**
```
Tests run: 1,  Failures: 0, Errors: 0 -- ProductosApplicationTests
Tests run: 6,  Failures: 0, Errors: 0 -- ProductosControllerTest
Tests run: 11, Failures: 0, Errors: 0 -- ProductosServiceTest

Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

> Los mensajes `ERROR` que aparecen en la consola durante los tests son **esperados** — son los propios logs del `GlobalExceptionHandler` siendo ejercitados por los tests de 404 y 400. No son fallas de tests.

---

#### Nota importante: Spring Boot 4.x vs versiones anteriores

Si buscas tutoriales o ejemplos de testing en Spring Boot, verás que usan `@MockBean`. En **Spring Boot 4.x** (que usa Spring Framework 7.x) esta anotación fue removida. Las equivalencias son:

| Spring Boot 3.x (anterior) | Spring Boot 4.x (este proyecto) |
|---|---|
| `@MockBean` | `@MockitoBean` |
| `import org.springframework.boot.test.mock.mockito.MockBean` | `import org.springframework.test.context.bean.override.mockito.MockitoBean` |
| `import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` | `import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` |

---

## 🚀 Crear un Nuevo Proyecto con Spring Initializr

### Opción 1: Web (Recomendado para principiantes)
1. Accede a: [start.spring.io](https://start.spring.io/)
2. Configura:
   - **Project**: Maven Project
   - **Language**: Java
   - **Spring Boot**: 4.0.5 o superior
   - **Group**: com.duoc
   - **Artifact**: productos
   - **Java Version**: 21
3. Dependencias (Add Dependencies):
   - Spring Web
   - Spring Boot Validation
   - Spring Data JPA
   - MySQL Driver
   - Lombok
4. Click "Generate"
5. Descomprime y abre en tu IDE favorito

### Opción 2: Línea de Comandos
```bash
curl https://start.spring.io/starter.zip \
  -d dependencies=web,validation,data-jpa,mysql,lombok \
  -d javaVersion=21 \
  -d bootVersion=4.0.5 \
  -d groupId=com.duoc \
  -d artifactId=productos \
  -o productos.zip

unzip productos.zip
cd productos
```

---

## ✅ Paso a Paso para Ejecutar el Proyecto

### Opción A: Ejecución local con Maven

### Paso 1: Clonar o Descargar el Proyecto
```bash
cd tu-directorio
git clone <repositorio>
cd productos
```

### Paso 2: Verificar Java
```bash
java -version
# Debe mostrar Java 21 o superior
```

### Paso 3: Crear la base de datos en MySQL
```sql
CREATE DATABASE productos;
```

### Paso 4: Configurar credenciales
Edita `src/main/resources/application.properties` con tu usuario y contraseña de MySQL.

### Paso 5: Compilar el Proyecto
```bash
mvn clean compile
```

### Paso 6: Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

**Salida esperada**:
```
[INFO] Started ProductosApplication in 2.5 seconds
```

La aplicación estará disponible en: `http://localhost:8080`

---

### Opción B: Ejecución con Docker 🐳

Esta opción empaqueta y ejecuta toda la aplicación dentro de contenedores Docker. No necesitas tener Java ni Maven instalados localmente.

#### Requisitos
- **Docker Desktop** instalado y corriendo

#### Paso 1: Levantar MySQL con Docker

Si aún no tienes el contenedor de MySQL, créalo:

```bash
docker run --name some-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=my-secret-pw \
  -d mysql
```

Luego crea la base de datos:

```bash
docker exec -it some-mysql mysql -uroot -pmy-secret-pw \
  -e "CREATE DATABASE IF NOT EXISTS productos;"
```

#### Paso 2: Construir la imagen de la API

Desde la raíz del proyecto (donde está el `Dockerfile`):

```bash
docker build -t productos-api .
```

Esto ejecuta una compilación en **dos etapas**:
1. **Builder**: Usa una imagen con JDK 21 para compilar el proyecto con Maven y generar el JAR
2. **Runtime**: Copia solo el JAR a una imagen liviana con JRE 21 (sin herramientas de compilación)

> La primera vez tarda un par de minutos mientras descarga dependencias. Las siguientes compilaciones son mucho más rápidas gracias al caché de capas de Docker.

#### Paso 3: Ejecutar la API especificando el puerto

```bash
docker run --name productos-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/productos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=my-secret-pw \
  -d productos-api
```

| Parámetro | Descripción |
|-----------|-------------|
| `-p 8080:8080` | Mapea el puerto `8080` del contenedor al puerto `8080` de tu máquina. Cambia el primer número para usar otro puerto local (ej: `-p 9090:8080`) |
| `host.docker.internal` | Dirección especial que permite al contenedor conectarse a servicios en tu máquina local (MySQL) |
| `-e SPRING_DATASOURCE_*` | Sobreescribe las variables del `application.properties` sin modificar el código |

**Salida esperada**:
```
Started ProductosApplication in 3.2 seconds
```

La aplicación estará disponible en: `http://localhost:8080`

#### Cambiar el puerto de la API

Si el puerto `8080` está ocupado, cambia solo el lado izquierdo del `-p`:

```bash
# Levantar la API en el puerto 9090
docker run --name productos-api \
  -p 9090:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/productos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=my-secret-pw \
  productos-api
```

La API estará disponible en: `http://localhost:9090`

#### Comandos útiles de Docker

```bash
docker stop productos-api      # Detener el contenedor
docker start productos-api     # Reiniciar el contenedor
docker logs productos-api      # Ver logs de la aplicación
docker rm productos-api        # Eliminar el contenedor (requiere stop previo)
docker rmi productos-api       # Eliminar la imagen
```

---

## 🧪 Pruebas de Endpoints

### Herramientas Recomendadas
- **Postman**: Interfaz gráfica completa
- **cURL**: Línea de comandos (incluido en macOS/Linux)
- **Thunder Client**: Extensión de VS Code
- **Insomnia**: Similar a Postman

### Resumen de Endpoints

| Método | URL | Descripción | Status Exitoso |
|--------|-----|-------------|----------------|
| POST | `/api/v1/productos` | Crear producto | 201 |
| GET | `/api/v1/productos` | Listar todos | 200 |
| GET | `/api/v1/productos?nombre=xyz` | Filtrar por nombre | 200 / 204 |
| GET | `/api/v1/productos/{id}` | Buscar por ID | 200 / 404 |
| PUT | `/api/v1/productos/{id}` | Actualizar producto | 200 / 404 |
| DELETE | `/api/v1/productos/{id}` | Eliminar producto | 204 / 404 |

### Ejemplos con cURL

#### 1. Crear un producto válido (POST)
```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Leche",
    "cantidad": 10,
    "precio": 1500,
    "categoria": "Electronics"
  }'
```

**Respuesta esperada (201)**:
```json
{
    "id": 1,
    "nombre": "Leche",
    "cantidad": 10,
    "precio": 1500,
    "categoria": "Electronics"
}
```

---

#### 2. Crear producto con datos inválidos (POST)
```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "",
    "cantidad": -5,
    "precio": null,
    "categoria": ""
  }'
```

**Respuesta esperada (400)**:
```json
{
    "nombre": "El nombre no puede estar vacío",
    "cantidad": "La cantidad debe ser mayor a cero",
    "precio": "El precio es obligatorio",
    "categoria": "La categoría no puede estar vacía"
}
```

---

#### 3. Listar todos los productos (GET)
```bash
curl -X GET http://localhost:8080/api/v1/productos
```

**Respuesta esperada (200)**:
```json
[
    {
        "id": 1,
        "nombre": "Leche",
        "cantidad": 10,
        "precio": 1500,
        "categoria": "Electronics"
    }
]
```

---

#### 4. Filtrar por nombre (GET con query param)
```bash
curl -X GET "http://localhost:8080/api/v1/productos?nombre=leche"
```

**Respuesta esperada (200)**:
```json
[
    {
        "id": 1,
        "nombre": "Leche",
        "cantidad": 10,
        "precio": 1500,
        "categoria": "Electronics"
    }
]
```

---

#### 5. Buscar por ID (GET)
```bash
curl -X GET http://localhost:8080/api/v1/productos/1
```

**Respuesta esperada (200)**:
```json
{
    "id": 1,
    "nombre": "Leche",
    "cantidad": 10,
    "precio": 1500,
    "categoria": "Electronics"
}
```

**Si no existe (404)**:
```json
{
    "error": "Producto no encontrado con id: 999"
}
```

---

#### 6. Actualizar un producto (PUT)
```bash
curl -X PUT http://localhost:8080/api/v1/productos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Leche Descremada",
    "cantidad": 20,
    "precio": 1800,
    "categoria": "Electronics"
  }'
```

**Respuesta esperada (200)**:
```json
{
    "id": 1,
    "nombre": "Leche Descremada",
    "cantidad": 20,
    "precio": 1800,
    "categoria": "Electronics"
}
```

---

#### 7. Eliminar un producto (DELETE)
```bash
curl -X DELETE http://localhost:8080/api/v1/productos/1
```

**Respuesta esperada (204)**: Sin cuerpo de respuesta.

**Si no existe (404)**:
```json
{
    "error": "Producto no encontrado con id: 1"
}
```

---

## 📊 Flujo de Datos en la Aplicación

```
┌─────────────────────────────────────────────────────────────────┐
│                   CLIENTE (Postman, cURL)                        │
└────────────────────────┬────────────────────────────────────────┘
                         │ 1. Envía JSON
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ProductosController                             │
│  - Recibe request HTTP                                           │
│  - Mapea JSON → ProductoRequest (@RequestBody)                   │
│  - Valida datos (@Valid)                                         │
│  - Si hay errores → GlobalExceptionHandler (captura)             │
└────────────────────────┬────────────────────────────────────────┘
                         │ 2. Si válido, llama al servicio
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ProductosService                                │
│  - Contiene lógica de negocio                                    │
│  - Convierte ProductoRequest → Productos (entidad)               │
│  - Convierte Productos → ProductoDTO (respuesta)                 │
│  - Lanza ProductoNotFoundException si no existe el recurso       │
└────────────────────────┬────────────────────────────────────────┘
                         │ 3. Persistencia
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ProductosRepository (JPA)                       │
│  - Extiende JpaRepository (CRUD automático)                      │
│  - Conecta con MySQL                                             │
│  - Hibernate genera y ejecuta el SQL                             │
└────────────────────────┬────────────────────────────────────────┘
                         │ 4. Respuesta
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ProductosController                             │
│  - ResponseEntity con HTTP Status (200, 201, 204, 400, 404)      │
│  - Serializa ProductoDTO → JSON                                  │
└────────────────────────┬────────────────────────────────────────┘
                         │ 5. Envía JSON
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   CLIENTE (Recibe respuesta)                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Patrón de Diseño CSR (Controller-Service-Repository)

**CSR** es la versión adaptada del patrón MVC específicamente para Spring Boot y APIs REST.

| Capa | Componente | Archivo | Responsabilidad |
|------|-----------|---------|-----------------|
| **Presentación** | **Controller** | `ProductosController.java` | Mapea rutas HTTP, recibe requests, retorna responses |
| **Lógica de Negocio** | **Service** | `ProductosService.java` | Contiene la lógica de negocio, conversión DTO ↔ Entidad |
| **Datos** | **Repository** | `ProductosRepository.java` | Acceso a MySQL mediante JPA, operaciones CRUD |
| **Datos** | **Model** | `Productos.java` | Entidad JPA mapeada a la tabla `productos` |
| **Datos** | **DTO** | `ProductoDTO / ProductoRequest` | Objetos de transferencia de datos (entrada y salida) |
| **Errores** | **Exception** | `GlobalExceptionHandler.java` | Captura excepciones y retorna respuestas coherentes |

### ¿Por qué CSR en lugar de MVC?

En aplicaciones REST con Spring Boot:
- **No hay "View" tradicional** (HTML) → La respuesta es **JSON**
- La **separación de responsabilidades** es más clara
- **Service** maneja la lógica de negocio (no el Controller)
- **Repository** aísla el acceso a datos
- Facilita **testing y mantenimiento**

### Ventajas del Patrón CSR

✅ Código más limpio y organizado  
✅ Fácil de testear (inyección de dependencias)  
✅ Escalable (añadir funcionalidades sin afectar otras capas)  
✅ Reutilizable (Service puede usarse desde múltiples Controllers)  
✅ Mantenible (cambios aislados por capa)

---

## 💡 Buenas Prácticas Implementadas

### 1. **Separación de Responsabilidades**
Cada clase tiene un único propósito claro:
- Controller: Mapeo HTTP, sin lógica de negocio
- Service: Lógica de negocio y conversión de datos
- Repository: Acceso a base de datos

### 2. **Patrón DTO**
Separa la entidad interna (`Productos`) del contrato externo (`ProductoDTO`/`ProductoRequest`), protegiendo el modelo interno.

### 3. **Excepción Personalizada**
`ProductoNotFoundException` en lugar de `NullPointerException` genérico, con mensajes descriptivos y manejo centralizado.

### 4. **Inyección de Dependencias**
```java
@Autowired
private ProductosService productosService;
```
Facilita: Testing, desacoplamiento, mantenibilidad.

### 5. **Validación de Datos a la Entrada**
```java
@PostMapping
public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request)
```
Previene procesamiento de datos inválidos antes de llegar al servicio.

### 6. **Códigos de Estado HTTP Apropiados**
- 201: Recurso creado (POST)
- 200: Operación exitosa (GET, PUT)
- 204: Sin contenido (lista vacía, DELETE)
- 400: Datos inválidos
- 404: Recurso no encontrado

### 7. **Versionamiento de API**
`/api/v1/productos` permite evolucionar la API sin romper clientes existentes.

### 8. **Sistema de Migraciones (Flyway)**
Usar Flyway en lugar de `ddl-auto=update` garantiza que todos los entornos (desarrollo, pruebas, producción) tienen exactamente el mismo esquema, con un historial auditable de todos los cambios aplicados.

### 9. **Validación contra Servicio Externo (OpenFeign)**
Delegar a una API de terceros la validación de datos de dominio (categorías) en lugar de mantener una lista hardcodeada en el código. El `CategoriaClient` se inyecta en el Service igual que el `ProductosRepository`: Spring genera la implementación, el Service solo consume el resultado.

### 10. **Logging Estructurado con SLF4J**
Usar `@Slf4j` y los diferentes niveles de log (`INFO`, `WARN`, `ERROR`) en lugar de `System.out.println()`. Los logs se guardan en archivo con rotación automática (`logs/productos.log`), lo que permite auditar el comportamiento de la aplicación sin detenerla.

```java
log.info("...");   // Eventos normales — flujo exitoso
log.warn("...");   // Situaciones inesperadas pero controladas
log.error("...");  // Errores que deben investigarse
```

### 11. **Documentación Automática con Swagger / SpringDoc OpenAPI**
Usar `@Tag`, `@Operation`, `@ApiResponses` y `@Schema` para documentar la API de forma que Swagger UI genere una interfaz interactiva automáticamente. Cualquier desarrollador puede explorar y probar los endpoints sin leer el código fuente.

```java
@Tag(name = "Productos", description = "Operaciones de gestión de productos")
@Operation(summary = "Crear un producto", description = "Crea un nuevo producto validando la categoría")
@ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
@Schema(description = "Nombre del producto", example = "Teclado Gamer Razer")
```

### 12. **Pruebas Unitarias con JUnit 5 y Mockito**
Escribir tests automatizados que verifiquen el comportamiento de cada método de forma aislada, usando mocks para simular dependencias (BD, API externa). Esto permite detectar errores antes de desplegar y refactorizar con confianza.

```java
// Service: prueba aislada con Mockito puro — sin Spring, sin BD
@ExtendWith(MockitoExtension.class)
class ProductosServiceTest { ... }  // 11 tests

// Controller: prueba de la capa web con MockMvc
@WebMvcTest(ProductosController.class)
class ProductosControllerTest { ... }  // 6 tests
```

---

### Nivel 1: Básico
1. Agregar un campo `descripcion` a `Productos` con validación `@NotBlank`
2. Crear un endpoint GET que retorne solo productos con `cantidad > 0`
3. Agregar validación de precio máximo con `@Max`

### Nivel 2: Intermedio
1. Implementar búsqueda combinada por nombre y precio usando `@Query` en el repository
2. Agregar paginación con `Pageable` en el endpoint de listar
3. Crear una excepción personalizada `ProductoDuplicadoException` para nombres repetidos

---

## 🐛 Solución de Problemas Comunes

### Error: "Unknown database 'productos'" al levantar el proyecto
Spring Boot intenta conectarse a la base de datos al iniciar. Si la base de datos `productos` no existe, la aplicación falla con este error:
```
java.sql.SQLSyntaxErrorException: Unknown database 'productos'
```
**Solución:** Crea la base de datos antes de levantar la aplicación.

Si usas Docker:
```bash
docker exec -it some-mysql mysql -uroot -pmy-secret-pw -e "CREATE DATABASE IF NOT EXISTS productos;"
```
Si usas MySQL local:
```sql
CREATE DATABASE IF NOT EXISTS productos;
```
> ⚠️ Hibernate **no crea las tablas** cuando se usa Flyway con `ddl-auto=none`. Es Flyway quien las crea al ejecutar `V1__create_productos_table.sql`. Debes crear la base de datos manualmente una sola vez.

---

### Error: "Communications link failure" (MySQL no está corriendo)
```bash
# Si usas Docker, verifica que el contenedor esté activo:
docker ps
docker start some-mysql

# Si usas MySQL local, verifica que el servicio esté corriendo:
mysql -u root -p
```

### Error: "Port 8080 already in use"
```properties
# Cambiar puerto en application.properties:
server.port=8081
```

### Error: "Field productosService required a bean"
Asegúrate de que `ProductosService` tiene `@Service` y Spring escanea el paquete correcto.

### Error: "JSON parse error"
El JSON enviado tiene formato inválido. Verifica comillas dobles, tipos de datos correctos y que el `Content-Type` sea `application/json`.

---

## 📝 Recursos Adicionales

- [Documentación oficial de Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/)
- [RESTful Web Services (HTTP Status Codes)](https://developer.mozilla.org/es/docs/Web/HTTP/Status)
- [Spring Initializr](https://start.spring.io/)
- [Flyway — Documentación oficial con Spring Boot y MySQL](https://documentation.red-gate.com/flyway/flyway-cli-and-api/usage/api-java/spring-boot)
- [🎬 Video del curso Fullstack I — Instalación, configuración e implementación de Flyway paso a paso](https://www.youtube.com/watch?v=WSnnJeqGtOQ)
- [Spring Cloud OpenFeign — Documentación oficial](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Platzi Fake Store API — Categorías en español](https://api.escuelajs.co/api/v1/categories)
- [SLF4J — Manual oficial (Simple Logging Facade for Java)](https://www.slf4j.org/manual.html)
- [Logback — Documentación oficial (motor de logs usado por Spring Boot)](https://logback.qos.ch/documentation.html)
- [Spring Boot Logging — Referencia oficial](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [Lombok `@Slf4j` — Documentación](https://projectlombok.org/features/log)
- [SpringDoc OpenAPI — Documentación oficial](https://springdoc.org/)
- [OpenAPI Specification (OAS) — Estándar oficial](https://swagger.io/specification/)
- [Swagger UI — Guía de uso](https://swagger.io/tools/swagger-ui/)
- [Anotaciones de SpringDoc — Referencia completa](https://docs.swagger.io/swagger-core/v2.0.0/apidocs/)
- [JUnit 5 — Documentación oficial](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito — Documentación oficial](https://site.mockito.org/)
- [Spring Testing — Referencia oficial](https://docs.spring.io/spring-framework/reference/testing.html)
- [Spring Boot Test — Guía de pruebas](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT, diseñado para propósitos educativos.

---
