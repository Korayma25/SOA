const vehiculoService = require('../services/vehiculoService');
const { publicarEvento } = require('../messaging/eventPublisher');
function mapVehiculo(row) {
  return {
    idVehiculo: Number(row.id_vehiculo ?? row.idVehiculo),
    marca: row.marca,
    modelo: row.modelo,
    placa: row.placa,
    chasis: row.chasis,
    anio: Number(row.anio),
    color: row.color ?? '',
    precio: Number(row.precio),
    estado: row.estado
  };
}

function convertirAFault(error) {
  const codigo = error.codigo || 'VEH-500';
  const mensaje = error.message || 'Error interno del servicio';

  return {
    Fault: {
      Code: {
        Value: error.name === 'NegocioError' ? 'soap:Sender' : 'soap:Receiver'
      },
      Reason: {
        Text: mensaje
      },
      Detail: {
        codigo,
        mensaje
      },
      statusCode: error.name === 'NegocioError' ? 400 : 500
    }
  };
}
// Publica el evento; si RabbitMQ falla, solo se registra el error.
async function notificar(tipo, vehiculoId, mensaje) {
  try {
    await publicarEvento(tipo, vehiculoId, mensaje);
  } catch (error) {
    console.error(`[RabbitMQ] No se pudo publicar el evento ${tipo}:`, error.message);
  }
}

async function ejecutar(fn) {
  try {
    return await fn();
  } catch (error) {
    console.error(error);
    throw convertirAFault(error);
  }
}

const service = {
  VehiculoService: {
    VehiculoPort: {
      ListarVehiculos() {
        return ejecutar(async () => {
          const rows = await vehiculoService.listarTodos();
          return {
            vehiculos: {
              vehiculo: rows.map(mapVehiculo)
            }
          };
        });
      },

      BuscarVehiculo(args) {
        return ejecutar(async () => {
          const row = await vehiculoService.buscarPorId(args.idVehiculo);
          return { vehiculo: mapVehiculo(row) };
        });
      },

      BuscarVehiculosPorMarca(args) {
        return ejecutar(async () => {
          const rows = await vehiculoService.buscarPorMarca(args.marca);
          return {
            vehiculos: {
              vehiculo: rows.map(mapVehiculo)
            }
          };
        });
      },

      CrearVehiculo(args) {
        return ejecutar(async () => {
          const row = await vehiculoService.crear(args.vehiculo);
          await notificar('REGISTRO', row.id_vehiculo, `Vehículo ${row.marca} ${row.modelo} registrado correctamente`);
          return {
            vehiculo: mapVehiculo(row),
            mensaje: 'Vehículo creado correctamente'
          };
        });
      },

      ActualizarVehiculo(args) {
        return ejecutar(async () => {
          const row = await vehiculoService.actualizar(args.idVehiculo, args.vehiculo);
          await notificar('ACTUALIZACION', row.id_vehiculo, `Vehículo ${row.marca} ${row.modelo} actualizado correctamente`);
          return {
            vehiculo: mapVehiculo(row),
            mensaje: 'Vehículo actualizado correctamente'
          };
        });
      },

      EliminarVehiculo(args) {
        return ejecutar(async () => {
          await vehiculoService.eliminar(args.idVehiculo);
          await notificar('ELIMINACION', args.idVehiculo, `Vehículo ${args.idVehiculo} eliminado correctamente`);
          return {
            success: true,
            mensaje: 'Vehículo eliminado correctamente'
          };
        });
      }
    }
  }
};

module.exports = service;