package ec.edu.uta.venta_vehiculos_api.dto;

import ec.edu.uta.venta_vehiculos_api.model.Vehiculo;

/**
 * Resultado interno de la capa de servicio: vehículo procesado + indicador
 * de si notificaciones-service confirmó el envío.
 */
public class ResultadoVehiculo {

    private final Vehiculo vehiculo;
    private final boolean notificacionEnviada;

    public ResultadoVehiculo(Vehiculo vehiculo, boolean notificacionEnviada) {
        this.vehiculo = vehiculo;
        this.notificacionEnviada = notificacionEnviada;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public boolean isNotificacionEnviada() {
        return notificacionEnviada;
    }
}
