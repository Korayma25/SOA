package ec.edu.uta.venta_vehiculos_api.dto;

/**
 * Respuesta común para las operaciones PUT y DELETE.
 */
public class OperacionVehiculoResponse {

    private boolean success;
    private String mensaje;
    private String notificacion;

    public OperacionVehiculoResponse() {
    }

    public OperacionVehiculoResponse(
            boolean success,
            String mensaje,
            String notificacion) {
        this.success = success;
        this.mensaje = mensaje;
        this.notificacion = notificacion;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }
}
