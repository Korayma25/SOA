package ec.edu.uta.venta_vehiculos_api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ec.edu.uta.venta_vehiculos_api.dto.NotificacionRequest;
import ec.edu.uta.venta_vehiculos_api.dto.NotificacionResponse;

/**
 * Cliente HTTP encargado de comunicarse con notificaciones-service.
 */
@Component
public class NotificacionClient {

    private final RestClient restClient;

    public NotificacionClient(
            @Value("${servicios.notificaciones.url}")
            String url) {

        this.restClient = RestClient
                .builder()
                .baseUrl(url)
                .build();
    }

    /**
     * Envía una notificación y devuelve true únicamente cuando el servicio
     * remoto confirma que fue procesada correctamente.
     *
     * Si notificaciones-service está apagado, no responde o devuelve error,
     * la operación principal del vehículo NO se revierte: simplemente se
     * retorna false para informar el fallo en el JSON de respuesta.
     */
    public boolean enviar(
            Integer vehiculoId,
            String tipo,
            String mensaje) {

        NotificacionRequest request =
                new NotificacionRequest(
                        vehiculoId,
                        "javier@uta.edu.ec",
                        tipo,
                        mensaje
                );

        try {

            NotificacionResponse respuesta =
                    restClient
                            .post()
                            .uri("/api/notificaciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(NotificacionResponse.class);

            boolean enviada =
                    respuesta != null
                            && respuesta.isSuccess()
                            && "ENVIADA".equalsIgnoreCase(
                                    respuesta.getEstado()
                            );

            if (respuesta != null) {
                System.out.println(
                        "NOTIFICACIÓN: "
                                + respuesta.getMensaje()
                );
            }

            return enviada;

        } catch (RestClientException e) {

            System.out.println(
                    "No se pudo contactar con "
                            + "notificaciones-service: "
                            + e.getMessage()
            );

            return false;
        }
    }
}
