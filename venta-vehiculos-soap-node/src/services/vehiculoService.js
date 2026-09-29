const repository = require('../repositories/vehiculoRepository');

class NegocioError extends Error {
  constructor(codigo, mensaje) {
    super(mensaje);
    this.name = 'NegocioError';
    this.codigo = codigo;
  }
}

function textoObligatorio(valor, campo, codigo) {
  if (valor === undefined || valor === null || String(valor).trim() === '') {
    throw new NegocioError(codigo, `${campo} es obligatorio`);
  }
}

function validarDatos(vehiculo) {
  textoObligatorio(vehiculo.marca, 'La marca', 'VEH-001');
  textoObligatorio(vehiculo.modelo, 'El modelo', 'VEH-002');
  textoObligatorio(vehiculo.placa, 'La placa', 'VEH-003');

  if (vehiculo.chasis === undefined || vehiculo.chasis === null || String(vehiculo.chasis).length !== 17) {
    throw new NegocioError('VEH-004', 'El chasis debe tener exactamente 17 caracteres');
  }

  const precio = Number(vehiculo.precio);
  if (!Number.isFinite(precio) || precio <= 0) {
    throw new NegocioError('VEH-005', 'El precio debe ser mayor que cero');
  }

  const anio = Number(vehiculo.anio);
  const anioMaximo = new Date().getFullYear() + 1;
  if (!Number.isInteger(anio) || anio < 1900 || anio > anioMaximo) {
    throw new NegocioError('VEH-006', 'El año del vehículo no es válido');
  }

  if (
    vehiculo.estado !== undefined &&
    vehiculo.estado !== null &&
    vehiculo.estado !== '' &&
    vehiculo.estado !== 'DISPONIBLE' &&
    vehiculo.estado !== 'VENDIDO'
  ) {
    throw new NegocioError('VEH-007', 'El estado debe ser DISPONIBLE o VENDIDO');
  }
}

function normalizar(vehiculo) {
  return {
    marca: String(vehiculo.marca),
    modelo: String(vehiculo.modelo),
    placa: String(vehiculo.placa),
    chasis: String(vehiculo.chasis),
    anio: Number(vehiculo.anio),
    color: vehiculo.color === undefined || vehiculo.color === null ? null : String(vehiculo.color),
    precio: Number(vehiculo.precio),
    estado: vehiculo.estado ? String(vehiculo.estado) : null
  };
}

async function listarTodos() {
  return repository.listarTodos();
}

async function buscarPorId(id) {
  const vehiculo = await repository.buscarPorId(Number(id));
  if (!vehiculo) {
    throw new NegocioError('VEH-404', 'Vehículo no encontrado');
  }
  return vehiculo;
}

async function buscarPorMarca(marca) {
  textoObligatorio(marca, 'La marca', 'VEH-008');
  return repository.buscarPorMarca(String(marca));
}

async function crear(datos) {
  validarDatos(datos);
  const vehiculo = normalizar(datos);

  if (await repository.buscarPorPlaca(vehiculo.placa)) {
    throw new NegocioError('VEH-409-PLACA', 'La placa ya se encuentra registrada');
  }

  if (await repository.buscarPorChasis(vehiculo.chasis)) {
    throw new NegocioError('VEH-409-CHASIS', 'El chasis ya se encuentra registrado');
  }

  if (!vehiculo.estado) {
    vehiculo.estado = 'DISPONIBLE';
  }

  return repository.crear(vehiculo);
}

async function actualizar(id, datos) {
  const idNumerico = Number(id);
  const actual = await buscarPorId(idNumerico);

  validarDatos(datos);
  const vehiculo = normalizar(datos);

  const porPlaca = await repository.buscarPorPlaca(vehiculo.placa);
  if (porPlaca && Number(porPlaca.id_vehiculo) !== idNumerico) {
    throw new NegocioError('VEH-409-PLACA', 'La placa pertenece a otro vehículo');
  }

  const porChasis = await repository.buscarPorChasis(vehiculo.chasis);
  if (porChasis && Number(porChasis.id_vehiculo) !== idNumerico) {
    throw new NegocioError('VEH-409-CHASIS', 'El chasis pertenece a otro vehículo');
  }

  if (!vehiculo.estado) {
    vehiculo.estado = actual.estado;
  }

  return repository.actualizar(idNumerico, vehiculo);
}

async function eliminar(id) {
  const vehiculo = await buscarPorId(Number(id));

  if (vehiculo.estado === 'VENDIDO') {
    throw new NegocioError('VEH-409-ESTADO', 'No se puede eliminar un vehículo vendido');
  }

  await repository.eliminar(Number(id));
  return true;
}

module.exports = {
  NegocioError,
  listarTodos,
  buscarPorId,
  buscarPorMarca,
  crear,
  actualizar,
  eliminar
};