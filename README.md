# 🏦 Banco Internacional - API Reclamos (Backend)

API REST para la gestión de reclamos de clientes del Banco Internacional. Arquitectura modular con seguridad JWT, Spring Boot 3 y PostgreSQL.

---

## Stack Tecnológico

| Tecnología        | Versión   | Uso                                    |
|-------------------|-----------|----------------------------------------|
| Java              | 21 (LTS)  | Lenguaje                               |
| Spring Boot       | 3.3.5     | Framework backend                      |
| Spring Security 6 | —         | Seguridad + JWT                        |
| Spring Data JPA   | —         | Acceso a datos                         |
| PostgreSQL        | 15        | Base de datos                          |
| Maven             | 3.9+      | Gestión de dependencias y build        |
| jjwt              | 0.12.6    | Generación y validación de JWT         |
| Lombok            | —         | Reducción de boilerplate               |
| SpringDoc OpenAPI | 2.6.0     | Swagger UI                             |
| JUnit 5 + Mockito | —         | Pruebas unitarias e integración        |

---

## Requisitos Previos

- **JDK 21** o superior
- **Maven 3.9** o superior
- **PostgreSQL 15** o superior (en ejecución)

---

## Estructura de Módulos

```
ec.banco.api-reclamos/                  ← POM Padre (aggregator)
│
├── api-commons/                        ← Módulo COMMONS (JAR librería)
│   └── ec.banco.commons.api_commons
│       ├── config/                     SecurityConfig, CorsConfig
│       ├── security/                   JwtService, JwtAuthenticationFilter
│       ├── exception/                  GlobalExceptionHandler, excepciones
│       └── dto/auth/                   LoginRequestDto, LoginResponseDto
│
├── api-reclamos-service/               ← Módulo NEGOCIO (Spring Boot App)
│   └── ec.banco.api_reclamos_service
│       ├── controller/                 AuthController, ClienteController, ReclamoController
│       ├── dto/                        ReclamoRequestDto, ClienteResponseDto, ReclamoResponseDto
│       ├── entity/                     Cliente, Reclamo
│       ├── enumerado/                      TipoReclamo
│       ├── repository/                 ClienteRepository, ReclamoRepository
│       ├── service/                    Interfaces + impl/
│       └── config/                     DataLoader, UserDetailsServiceImpl, OpenApiConfig
│
├── reclamos-ear/                       ← Módulo EAR
└── scripts/                            ← Scripts de base de datos
```

---

## Configuración de Base de Datos (PostgreSQL)

### Paso 1 — Crear el usuario de base de datos

Conectarse a PostgreSQL como superusuario y ejecutar:

```sql
-- Crear el usuario
CREATE USER user_reclamos WITH PASSWORD 'useradmin';

-- Otorgar permisos para crear bases de datos
ALTER USER user_reclamos CREATEDB;
```

### Paso 2 — Crear la base de datos

```sql
-- Crear la base de datos asignada al nuevo usuario
CREATE DATABASE banco_reclamos OWNER user_reclamos;

-- Otorgar todos los privilegios
GRANT ALL PRIVILEGES ON DATABASE banco_reclamos TO user_reclamos;
```

### Paso 3 — Otorgar permisos sobre el esquema public

```sql
-- Conectar a la base de datos
\c banco_reclamos;

-- Otorgar permisos en el esquema
GRANT ALL ON SCHEMA public TO user_reclamos;
```

### Ejecución psql

```bash
psql -U postgres
```

```sql
CREATE USER user_reclamos WITH PASSWORD 'useradmin';
ALTER USER user_reclamos CREATEDB;
CREATE DATABASE banco_reclamos OWNER user_reclamos;
GRANT ALL PRIVILEGES ON DATABASE banco_reclamos TO user_reclamos;
\c banco_reclamos;
GRANT ALL ON SCHEMA public TO user_reclamos;
\q
```

### Sobre las tablas

**No es necesario crear las tablas manualmente.** Hibernate (`spring.jpa.hibernate.ddl-auto=update`) crea las tablas automáticamente al iniciar la aplicación. Las tablas que se generan son:

| Tabla       | Descripción                                |
|-------------|--------------------------------------------|
| `clientes`  | Datos de clientes + credenciales de acceso |
| `reclamos`  | Reclamos registrados por los clientes      |

### Sobre los datos iniciales

**No es necesario insertar registros manualmente.** La clase `DataLoader.java` se ejecuta automáticamente al arrancar la API e inserta los siguientes registros de prueba si las tablas están vacías:

#### Clientes de prueba (se cargan automáticamente)

| Cédula       | Nombres         | Apellidos          | Contraseña  |
|--------------|-----------------|--------------------|-------------|
| 1712345678   | Juan Carlos     | Pérez López        | juan1234    |
| 1798765432   | María Elena     | González Ruiz      | maria1234   |
| 0501234567   | Carlos Andrés   | Ramírez Torres     | carlos1234  |
| 0912345678   | Ana Gabriela    | Mendoza Sánchez    | ana1234     |
| 1104567890   | Luis Fernando   | Castillo Herrera   | luis1234    |

> **Nota:** Las contraseñas se almacenan encriptadas con BCrypt. Los valores mostrados son los passwords en texto plano para uso en el login.

---

## Configuración de la Aplicación

El archivo `application.properties` debe tener la configuración del usuario de base de datos creado:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banco_reclamos
spring.datasource.username=user_reclamos
spring.datasource.password=useradmin
```

---

## Instalación y Ejecución

### 1. Compilar el proyecto

```bash
# Desde la raíz del proyecto (compila commons primero, luego reclamos-service)
mvn clean install -DskipTests
```

### 2. Ejecutar la aplicación

```bash
cd api-reclamos-service
mvn spring-boot:run
```

La API estará disponible en: **http://localhost:8080**

### 4. Verificar funcionamiento

```bash
# Probar el login (no requiere JWT)
curl -X POST http://localhost:8080/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identificacion": "1712345678", "password": "juan1234"}'
```

Respuesta esperada:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "identificacion": "1712345678",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez López",
  "expiresIn": 3600000
}
```

---

## Swagger / OpenAPI

Una vez que la aplicación esté en ejecución, acceder a:

| Recurso      | URL                                          |
|--------------|----------------------------------------------|
| Swagger UI   | http://localhost:8080/swagger-ui.html         |
| API Docs     | http://localhost:8080/v3/api-docs             |

---

## Endpoints REST

| Método | Endpoint                 | Auth  | Descripción                |
|--------|--------------------------|-------|----------------------------|
| POST   | `/v1/api/auth/login`        | ✗     | Login → retorna JWT        |
| GET    | `/v1/api/clientes/{cedula}` | ✓ JWT | Consultar cliente por ID   |
| POST   | `/v1/api/reclamos`          | ✓ JWT | Registrar nuevo reclamo    |

### Ejemplos de uso con cURL

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identificacion": "1712345678", "password": "juan1234"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 2. Consultar cliente
curl -X GET http://localhost:8080/v1/api/clientes/1712345678 \
  -H "Authorization: Bearer $TOKEN"

# 3. Registrar reclamo
curl -X POST http://localhost:8080/v1/api/reclamos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "identificacionCliente": "1712345678",
    "tipoReclamo": "TARJETAS_CREDITO",
    "detalleReclamo": "Cobro duplicado en mi tarjeta de crédito por $45.50"
  }'
```

---

## Pruebas

```bash
# Ejecutar solo pruebas unitarias
mvn test

# Ejecutar pruebas unitarias + integración
mvn verify
```

### Cobertura de pruebas

| Tipo         | Clase                          |
|--------------|--------------------------------|
| Unitaria     | `ClienteServiceImplTest`       |
| Unitaria     | `ReclamoServiceImplTest`       |
| Unitaria     | `AuthServiceImplTest`          |
| Integración  | `AuthControllerIntegrationTest`|
| Integración  | `ReclamosIntegrationTest`      |

> Las pruebas de integración utilizan **H2 en memoria** y no requieren PostgreSQL.

---

## Generar EAR

```bash
# 1. Dentro del proyecto raiz ejecutar el siguiente comando
mvn clean package -DskipTests

# 2. Resultado
# api-reclamos/reclamos-ear/reclamos-ear-1.0.0.ear
```

## EAR

El archivo .ear se se encontrará dentro de este repositorio en la ruta principal del proyecto con el nombre `realamos-ear-1.0.0.ear`
