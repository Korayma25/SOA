# notificaciones-service

Microservicio REST independiente utilizado por `venta-vehiculos-api_security` para procesar las notificaciones de la TAREA 04.

## Requisitos

- Java 17
- Maven

## Ejecutar

```bash
mvn spring-boot:run
```

El servicio queda disponible en:

```text
http://localhost:8081
```

## Endpoint

```http
POST http://localhost:8081/api/notificaciones
Content-Type: application/json
```

### Ejemplo

```json
{
  "vehiculoId": 5,
  "destinatario": "usuario@correo.com",
  "tipo": "ACTUALIZACION",
  "mensaje": "Vehículo actualizado correctamente"
}
```

Respuesta esperada: **201 Created**

```json
{
  "success": true,
  "estado": "ENVIADA",
  "mensaje": "Notificación procesada correctamente"
}
```

## Tipos admitidos

- `REGISTRO`
- `ACTUALIZACION`
- `ELIMINACION`

En esta práctica el microservicio simula el procesamiento de la notificación y la muestra en consola; no envía un correo o SMS real.
