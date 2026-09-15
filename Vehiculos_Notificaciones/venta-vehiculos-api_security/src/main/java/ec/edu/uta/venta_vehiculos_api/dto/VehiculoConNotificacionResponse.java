package ec.edu.uta.venta_vehiculos_api.dto;

import java.math.BigDecimal;

import ec.edu.uta.venta_vehiculos_api.model.Vehiculo;

/**
 * DTO utilizado como respuesta del POST de vehículos.
 * Mantiene los datos del vehículo y agrega el resultado del envío
 * de la notificación sin mezclar esa información con la entidad JPA.
 */
public class VehiculoConNotificacionResponse {

    private Integer idVehiculo;
    private String marca;
    private String modelo;
    private String placa;
    private String chasis;
    private Integer anio;
    private String color;
    private BigDecimal precio;
    private String estado;
    private String notificacion;

    public VehiculoConNotificacionResponse() {
    }

    public VehiculoConNotificacionResponse(Vehiculo vehiculo, String notificacion) {
        this.idVehiculo = vehiculo.getIdVehiculo();
        this.marca = vehiculo.getMarca();
        this.modelo = vehiculo.getModelo();
        this.placa = vehiculo.getPlaca();
        this.chasis = vehiculo.getChasis();
        this.anio = vehiculo.getAnio();
        this.color = vehiculo.getColor();
        this.precio = vehiculo.getPrecio();
        this.estado = vehiculo.getEstado();
        this.notificacion = notificacion;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getChasis() {
        return chasis;
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }
}
