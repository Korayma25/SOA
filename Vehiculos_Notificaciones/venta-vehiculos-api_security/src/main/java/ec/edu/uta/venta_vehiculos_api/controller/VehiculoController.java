package ec.edu.uta.venta_vehiculos_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.uta.venta_vehiculos_api.dto.OperacionVehiculoResponse;
import ec.edu.uta.venta_vehiculos_api.dto.ResultadoEliminacion;
import ec.edu.uta.venta_vehiculos_api.dto.ResultadoVehiculo;
import ec.edu.uta.venta_vehiculos_api.dto.VehiculoConNotificacionResponse;
import ec.edu.uta.venta_vehiculos_api.model.Vehiculo;
import ec.edu.uta.venta_vehiculos_api.service.VehiculoService;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> listar(
            @RequestParam(required = false)
            String marca) {

        if (marca != null && !marca.isBlank()) {
            return ResponseEntity.ok(
                    service.buscarPorMarca(marca)
            );
        }

        return ResponseEntity.ok(
                service.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> buscar(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );
    }

    /**
     * POST /api/vehiculos
     *
     * La creación del vehículo se considera exitosa aunque el servicio de
     * notificaciones esté caído. El campo "notificacion" indica el resultado.
     */
    @PostMapping
    public ResponseEntity<VehiculoConNotificacionResponse> crear(
            @RequestBody Vehiculo vehiculo) {

        ResultadoVehiculo resultado =
                service.crear(vehiculo);

        String notificacion =
                resultado.isNotificacionEnviada()
                        ? "Vehículo registrado correctamente - notificación enviada"
                        : "Vehículo registrado correctamente - notificación no enviada";

        VehiculoConNotificacionResponse respuesta =
                new VehiculoConNotificacionResponse(
                        resultado.getVehiculo(),
                        notificacion
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    /**
     * PUT /api/vehiculos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<OperacionVehiculoResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody Vehiculo vehiculo) {

        ResultadoVehiculo resultado =
                service.actualizar(id, vehiculo);

        String notificacion =
                resultado.isNotificacionEnviada()
                        ? "Notificación enviada correctamente"
                        : "Notificación no enviada";

        return ResponseEntity.ok(
                new OperacionVehiculoResponse(
                        true,
                        "Vehículo actualizado correctamente",
                        notificacion
                )
        );
    }

    /**
     * DELETE /api/vehiculos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<OperacionVehiculoResponse> eliminar(
            @PathVariable Integer id) {

        try {
            ResultadoEliminacion resultado =
                    service.eliminar(id);

            String notificacion =
                    resultado.isNotificacionEnviada()
                            ? "Notificación enviada correctamente"
                            : "Notificación no enviada";

            return ResponseEntity.ok(
                    new OperacionVehiculoResponse(
                            true,
                            "Vehículo eliminado correctamente",
                            notificacion
                    )
            );

        } catch (RuntimeException e) {

            // Si la eliminación no se ejecutó, tampoco se intenta notificar.
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new OperacionVehiculoResponse(
                                    false,
                                    "Vehículo no eliminado",
                                    "Notificación no enviada"
                            )
                    );
        }
    }
}
