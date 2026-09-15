package ec.edu.uta.venta_vehiculos_api.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.uta.venta_vehiculos_api.client.NotificacionClient;
import ec.edu.uta.venta_vehiculos_api.dto.ResultadoEliminacion;
import ec.edu.uta.venta_vehiculos_api.dto.ResultadoVehiculo;
import ec.edu.uta.venta_vehiculos_api.model.Vehiculo;
import ec.edu.uta.venta_vehiculos_api.repository.VehiculoRepository;

@Service
public class VehiculoService {

    private final VehiculoRepository repository;
    private final NotificacionClient notificacionClient;

    public VehiculoService(
            VehiculoRepository repository,
            NotificacionClient notificacionClient) {

        this.repository = repository;
        this.notificacionClient = notificacionClient;
    }

    public List<Vehiculo> listarTodos() {
        return repository.findAll();
    }

    public Vehiculo buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehículo no encontrado"
                        )
                );
    }

    public List<Vehiculo> buscarPorMarca(String marca) {
        return repository
                .findByMarcaContainingIgnoreCase(marca);
    }

    /**
     * Registra el vehículo y luego intenta notificar el evento REGISTRO.
     */
    public ResultadoVehiculo crear(Vehiculo vehiculo) {

        validarDatos(vehiculo);

        if (repository.findByPlaca(
                vehiculo.getPlaca()).isPresent()) {

            throw new IllegalArgumentException(
                    "La placa ya se encuentra registrada"
            );
        }

        if (repository.findByChasis(
                vehiculo.getChasis()).isPresent()) {

            throw new IllegalArgumentException(
                    "El chasis ya se encuentra registrado"
            );
        }

        if (vehiculo.getEstado() == null ||
                vehiculo.getEstado().isBlank()) {

            vehiculo.setEstado("DISPONIBLE");
        }

        Vehiculo creado = repository.save(vehiculo);

        boolean notificacionEnviada =
                notificacionClient.enviar(
                        creado.getIdVehiculo(),
                        "REGISTRO",
                        "Vehículo registrado correctamente: "
                                + creado.getPlaca()
                );

        return new ResultadoVehiculo(
                creado,
                notificacionEnviada
        );
    }

    /**
     * Actualiza el vehículo y, solamente después de guardar correctamente,
     * intenta notificar el evento ACTUALIZACION.
     */
    public ResultadoVehiculo actualizar(
            Integer id,
            Vehiculo datos) {

        Vehiculo actual = buscarPorId(id);

        validarDatos(datos);

        repository.findByPlaca(datos.getPlaca())
                .ifPresent(encontrado -> {

                    if (!encontrado.getIdVehiculo()
                            .equals(id)) {

                        throw new IllegalArgumentException(
                                "La placa pertenece a otro vehículo"
                        );
                    }
                });

        repository.findByChasis(datos.getChasis())
                .ifPresent(encontrado -> {

                    if (!encontrado.getIdVehiculo()
                            .equals(id)) {

                        throw new IllegalArgumentException(
                                "El chasis pertenece a otro vehículo"
                        );
                    }
                });

        actual.setMarca(datos.getMarca());
        actual.setModelo(datos.getModelo());
        actual.setPlaca(datos.getPlaca());
        actual.setChasis(datos.getChasis());
        actual.setAnio(datos.getAnio());
        actual.setColor(datos.getColor());
        actual.setPrecio(datos.getPrecio());

        if (datos.getEstado() != null) {
            actual.setEstado(datos.getEstado());
        }

        Vehiculo actualizado = repository.save(actual);

        boolean notificacionEnviada =
                notificacionClient.enviar(
                        actualizado.getIdVehiculo(),
                        "ACTUALIZACION",
                        "Vehículo actualizado correctamente: "
                                + actualizado.getPlaca()
                );

        return new ResultadoVehiculo(
                actualizado,
                notificacionEnviada
        );
    }

    /**
     * Elimina el vehículo y, únicamente si la eliminación se ejecutó,
     * intenta notificar el evento ELIMINACION.
     */
    public ResultadoEliminacion eliminar(Integer id) {

        Vehiculo vehiculo = buscarPorId(id);

        String placa = vehiculo.getPlaca();
        Integer vehiculoId = vehiculo.getIdVehiculo();

        repository.delete(vehiculo);
        repository.flush();

        boolean notificacionEnviada =
                notificacionClient.enviar(
                        vehiculoId,
                        "ELIMINACION",
                        "Vehículo eliminado correctamente: "
                                + placa
                );

        return new ResultadoEliminacion(
                notificacionEnviada
        );
    }

    private void validarDatos(Vehiculo vehiculo) {

        if (vehiculo.getMarca() == null ||
                vehiculo.getMarca().isBlank()) {

            throw new IllegalArgumentException(
                    "La marca es obligatoria"
            );
        }

        if (vehiculo.getModelo() == null ||
                vehiculo.getModelo().isBlank()) {

            throw new IllegalArgumentException(
                    "El modelo es obligatorio"
            );
        }

        if (vehiculo.getPlaca() == null ||
                vehiculo.getPlaca().isBlank()) {

            throw new IllegalArgumentException(
                    "La placa es obligatoria"
            );
        }

        if (vehiculo.getChasis() == null ||
                vehiculo.getChasis().length() != 17) {

            throw new IllegalArgumentException(
                    "El chasis debe tener exactamente 17 caracteres"
            );
        }

        if (vehiculo.getPrecio() == null ||
                vehiculo.getPrecio()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero"
            );
        }

        int anioMaximo =
                Year.now().getValue() + 1;

        if (vehiculo.getAnio() == null ||
                vehiculo.getAnio() < 1900 ||
                vehiculo.getAnio() > anioMaximo) {

            throw new IllegalArgumentException(
                    "El año del vehículo no es válido"
            );
        }

        if (vehiculo.getEstado() != null &&
                !vehiculo.getEstado()
                        .equals("DISPONIBLE") &&
                !vehiculo.getEstado()
                        .equals("VENDIDO")) {

            throw new IllegalArgumentException(
                    "El estado debe ser DISPONIBLE o VENDIDO"
            );
        }
    }
}
