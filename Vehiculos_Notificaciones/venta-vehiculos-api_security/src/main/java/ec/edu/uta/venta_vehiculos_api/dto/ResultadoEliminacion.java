package ec.edu.uta.venta_vehiculos_api.dto;

/**
 * Resultado interno de una eliminación exitosa.
 */
public class ResultadoEliminacion {

    private final boolean notificacionEnviada;

    public ResultadoEliminacion(boolean notificacionEnviada) {
        this.notificacionEnviada = notificacionEnviada;
    }

    public boolean isNotificacionEnviada() {
        return notificacionEnviada;
    }
}
