# Prueba Técnica - API de Gestión de Proveedores y Órdenes de Pago

API REST en Spring Boot para la gestión de proveedores y sus órdenes de pago. Cubre el ciclo CRUD de ambos recursos, cambio de estado controlado por reglas de transición, y listados paginados con filtros opcionales.

## Requisitos previos

- Java 25
- Maven (incluido vía wrapper `mvnw`, no requiere instalación)

## Ejecutar localmente

```bash
# Linux / Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La aplicación queda escuchando en `http://localhost:8080`.

La base de datos es **H2 en memoria**, se reinicia con cada arranque. La consola web de H2 está disponible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:pruebatecnica`, usuario: `sa`, sin contraseña).

## Ejecutar las pruebas

```bash
./mvnw test
```

## Swagger / OpenAPI

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **JSON OpenAPI**: http://localhost:8080/v3/api-docs

## Decisiones de diseño

### Arquitectura por capas (controller → service → repository)
Separación clásica con DTOs en los bordes y entidades JPA solo en la capa de persistencia. El controller nunca expone entidades; un mapper traduce entre ambos mundos. Esto evita filtrar el modelo de BD al cliente y simplifica los cambios de schema.

### DTOs como records
Para los DTOs nuevos se usan `record` en lugar de clases con Lombok. Se utilizan por su inmutabilidad por defecto, accessors automáticos, deserialización con Jackson sin configuración extra, menos código repetido.

### Mapper como `@Component` (no estático)
Esto permite inyectar dependencias en el futuro y mantiene el estilo Spring DI. También encapsula el mapeo en un único lugar.

### Estado inicial de `OrdenPago` forzado a `BORRADOR` en el backend
El DTO de creación no acepta `estado`; el mapper lo asigna explícitamente. La idea es cumplir el requerimiento sin depender del cliente y evitar que se salten el flujo de transiciones.

### `idTributario` y `proveedor` inmutables
`@Column(updatable = false)` en `Proveedor.idTributario` y en el `@JoinColumn` de `OrdenPago.proveedor`. El RUT identifica legalmente al proveedor (no cambia en la realidad), y una orden no debería poder "cambiar de proveedor" una vez creada, pues debería ser una orden distinta.

### Transiciones de estado de `OrdenPago` modeladas en el enum
La tabla de transiciones válidas vive en `EstadoOrdenPago` con un método `puedeTransicionarA`. Esto es por cohesión (el enum sabe sus propias reglas), reusabilidad y único punto de cambio si las reglas cambian.

### Relación `@ManyToOne` entre `OrdenPago` y `Proveedor`
En lugar de un UUID suelto, la orden referencia al objeto `Proveedor` (lazy). Esto provee integridad referencial garantizada por la BD (foreign key) y acceso directo al objeto cuando se necesite.

### Códigos HTTP
- `201 Created` con `Location` y body en POST.
- `200 OK` con el recurso actualizado en PUT y PATCH.
- `404 Not Found` solo cuando un recurso identificado por id no existe.
- `200 OK` con `[]` cuando un listado/filtro no arroja resultados (no `404`).
- `400 Bad Request` para errores de validación de input.
- `409 Conflict` para violaciones de constraint único (ej: `idTributario` duplicado).
- `422 Unprocessable Content` para reglas de negocio violadas (proveedor no activo, transición inválida).

### Paginación con wrapper propio (`PageResponse<T>`)
En lugar de exponer `Page<T>` de Spring Data al cliente (que filtra detalles internos como `pageable.sort`), se devuelve un wrapper plano con `content`, `page`, `size`, `totalElements`, `totalPages`. Esto permite una API más limpia y estable frente a cambios internos de Spring Data.

### `size` máximo de página: 50, default: 20
El controller normaliza valores fuera de rango silenciosamente (página negativa → 0, size > 50 → 50). Este límite protege a la BD de queries con tamaños arbitrarios y evita 400s por valores que el cliente puede haber escrito por error.

### Manejo de concurrencia con optimistic locking en `OrdenPago`
Se agregó `@Version` a la entidad. Si dos requests intentan modificar simultáneamente la misma orden (ej: dos usuarios aprobándola), Hibernate detecta el conflicto en el UPDATE: solo uno gana y el otro recibe `409 Conflict` con mensaje descriptivo (manejado en `GlobalExceptionHandler` vía `ObjectOptimisticLockingFailureException`). Es la forma más liviana de manejar concurrencia: no bloquea filas en BD, solo detecta y reporta el conflicto. Aplica especialmente al PATCH de transición de estado, donde un mismo recurso puede ser tocado por múltiples actores.

## Pendientes

- **Idempotencia en creación de órdenes vía `Idempotency-Key`** (Bloque 4): no se implementó por tiempo. Una solución robusta requiere persistir las keys con sus respuestas, manejar TTL para evitar acumulación, y resolver carreras entre el chequeo de la key y el guardado del resultado. Una implementación apurada sería peor que no incluirla, por lo que se priorizó dejar fuera esta funcionalidad antes que hacerla a medias.
- **Endpoint de órdenes próximas a vencer** (Bloque 4): no se implementó por tiempo. Habría requerido definir y documentar una regla de negocio propia (el modelo no tiene `fechaVencimiento`), por ejemplo "órdenes en estado `BORRADOR` o `APROBADA` con `fechaCreacion` más antigua que N días". Se priorizó cerrar concurrencia y reporte agregado, que aportaron más valor por menos tiempo.
