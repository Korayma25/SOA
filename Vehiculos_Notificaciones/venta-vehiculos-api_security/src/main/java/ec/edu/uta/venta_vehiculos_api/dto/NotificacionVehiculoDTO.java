package ec.edu.uta.venta_vehiculos_api.dto;

import java.io.Serializable;

public class NotificacionVehiculoDTO implements Serializable {

    private Long vehiculoId;
    private String tipo;
    private String mensaje;

    public NotificacionVehiculoDTO() {}

    public NotificacionVehiculoDTO(Long vehiculoId, String tipo, String mensaje) {
        this.vehiculoId = vehiculoId;
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}