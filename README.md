# VidaSalud - Backend For Frontend (BFF)

Backend For Frontend de la plataforma **VidaSalud**.

Este servicio funciona como punto de entrada para el frontend Angular y centraliza la comunicación con los microservicios internos de la solución.

Entre sus principales responsabilidades se encuentran la autenticación, autorización por roles, manejo de CORS, propagación del JWT y comunicación con los microservicios de Catálogo y Atenciones.

## Arquitectura

```text
                    Microsoft Entra ID
                           │
                           │ JWT
                           ▼
┌─────────────────────────────────────────┐
│          Frontend VidaSalud             │
│               Angular                   │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│          ms-vidasalud-bff               │
│                                         │
│  Autenticación                          │
│  Autorización por roles                 │
│  CORS                                   │
│  Proxy / Routing                        │
│  Propagación del JWT                    │
└────────────┬─────────────────┬──────────┘
             │                 │
             ▼                 ▼
   ms-vidasalud-        ms-vidasalud-
     catalog             appointments
             │                 │
             └────────┬────────┘
                      ▼
                Oracle Database
```

## Tecnologías

- Java 17+
- Spring Boot 4.0.8
- Spring Web MVC
- Spring Security
- OAuth2 Resource Server
- Microsoft Entra ID / Azure AD
- Spring RestClient
- Maven
- Docker

> La imagen Docker utiliza Eclipse Temurin JDK/JRE 21.

## Puerto

La aplicación está configurada para ejecutarse en:

```text
8090
```

Base URL local:

```text
http://localhost:8090
```

## Microservicios utilizados

El BFF se conecta actualmente con:

```text
Appointments → http://localhost:8081
Catalog      → http://localhost:8091
```

Las URLs se pueden modificar mediante variables de entorno.

## Variables de entorno

| Variable | Descripción |
|---|---|
| `AZURE_TENANT_ID` | Tenant de Microsoft Entra ID |
| `AZURE_CLIENT_ID` | Client ID de la API |
| `AZURE_APP_ID_URI` | Application ID URI |
| `APPOINTMENTS_URL` | URL de `ms-vidasalud-appointments` |
| `CATALOG_URL` | URL de `ms-vidasalud-catalog` |

Ejemplo:

```bash
APPOINTMENTS_URL=http://localhost:8081
CATALOG_URL=http://localhost:8091
```

## Seguridad

El BFF funciona como **OAuth2 Resource Server** y valida los Access Tokens emitidos por Microsoft Entra ID.

El token se recibe mediante:

```http
Authorization: Bearer <access_token>
```

Además de validar el token, el BFF aplica autorización utilizando los App Roles definidos en Microsoft Entra ID.

## Roles

La aplicación utiliza actualmente los siguientes roles:

```text
Admin
Operador
Cliente
Auditor
```

Spring Security recibe estos roles como authorities:

```text
APPROLE_Admin
APPROLE_Operador
APPROLE_Cliente
APPROLE_Auditor
```

## Permisos principales

| Recurso | Admin | Operador | Cliente | Auditor |
|---|:---:|:---:|:---:|:---:|
| Consultar atenciones | ✅ | ✅ | ✅ | ❌ |
| Crear atención | ✅ | ✅ | ✅ | ❌ |
| Modificar atención | ✅ | ✅ | ❌ | ❌ |
| Cambiar estado | ✅ | ✅ | ❌ | ❌ |
| Consultar catálogo | ✅ | ✅ | ❌ | ❌ |
| Crear/modificar catálogo | ✅ | ❌ | ❌ | ❌ |
| Reportería | ✅ | ❌ | ❌ | ❌ |
| Auditoría | ✅ | ❌ | ❌ | ✅ |

## API de Atenciones

El frontend consume:

```text
/api/appointments
```

El BFF traduce las solicitudes al microservicio:

```text
/api/atenciones
```

Endpoints:

```http
GET  /api/appointments
POST /api/appointments
GET  /api/appointments/{id}
PUT  /api/appointments/{id}
PUT  /api/appointments/{id}/status
```

Para el cambio de estado, el frontend envía:

```json
{
  "status": "CONFIRMADA"
}
```

y el BFF lo transforma para el microservicio a:

```json
{
  "nuevoEstado": "CONFIRMADA"
}
```

## API de Catálogo

```http
GET  /api/catalog/services
POST /api/catalog/services
GET  /api/catalog/services/{id}
PUT  /api/catalog/services/{id}

GET  /api/catalog/boxes
GET  /api/catalog/boxes/{id}
POST /api/catalog/boxes

GET /api/catalog/cupos
GET /api/catalog/cupos/box/{boxId}/fecha/{fecha}
PUT /api/catalog/cupos/{id}
```

## Endpoints de validación

Atenciones:

```http
GET /api/appointments/ping
```

Catálogo:

```http
GET /api/catalog/ping
```

Reportería:

```http
GET /api/report/ping
```

Estado del BFF:

```http
GET /api/bff/status
```

## Comunicación con microservicios

El BFF utiliza `RestClient` de Spring para comunicarse con los servicios internos.

El header:

```http
Authorization
```

es propagado a los microservicios para mantener la identidad del usuario durante todo el flujo.

Ejemplo:

```text
Angular
  │
  │ Bearer Token
  ▼
BFF
  │
  │ Bearer Token
  ▼
Appointments / Catalog
```

Los códigos HTTP y cuerpos de respuesta provenientes de los microservicios son reenviados al frontend.

Si alguno de los microservicios no se encuentra disponible, el BFF responde con:

```text
503 Service Unavailable
```

## CORS

Actualmente se permiten solicitudes desde los orígenes definidos en `SecurityConfig`.

Para desarrollo local se encuentra habilitado:

```text
http://localhost:4200
```

Los orígenes productivos deben mantenerse sincronizados con las URLs utilizadas para desplegar el frontend.

## Ejecución local

### Windows

El repositorio incluye:

```text
run-dev.cmd
```

También puede ejecutarse mediante:

```bash
mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

## Compilar

```bash
./mvnw clean package
```

## Docker

Construir imagen:

```bash
docker build -t vidasalud-bff .
```

Ejecutar utilizando el puerto configurado por Spring:

```bash
docker run \
  -p 8090:8090 \
  -e APPOINTMENTS_URL=http://appointments:8081 \
  -e CATALOG_URL=http://catalog:8091 \
  vidasalud-bff
```

## Estructura

```text
src/main/java/cl/duoc/ms_vidasalud_bff/
├── config/
│   ├── RestClientConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AppointmentsController.java
│   ├── BffStatusController.java
│   ├── CatalogController.java
│   └── ReportController.java
└── MsVidasaludBffApplication.java
```

## Responsabilidad del BFF

El BFF evita que el frontend tenga que conocer directamente la ubicación de cada microservicio.

Esto permite centralizar:

```text
Autenticación
Autorización
Roles
CORS
Routing
Comunicación entre servicios
Manejo de errores
```

## Proyecto VidaSalud

Este repositorio forma parte de la arquitectura de microservicios de **VidaSalud** y representa la capa de integración entre la interfaz de usuario y los servicios backend.
