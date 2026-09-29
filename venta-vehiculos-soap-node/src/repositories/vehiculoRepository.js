const db = require('../config/db');

async function listarTodos() {
  const [rows] = await db.execute(`
    SELECT id_vehiculo, marca, modelo, placa, chasis, anio, color, precio, estado
    FROM vehiculos
    ORDER BY id_vehiculo
  `);
  return rows;
}

async function buscarPorId(id) {
  const [rows] = await db.execute(`
    SELECT id_vehiculo, marca, modelo, placa, chasis, anio, color, precio, estado
    FROM vehiculos
    WHERE id_vehiculo = ?
  `, [id]);
  return rows[0] || null;
}

async function buscarPorMarca(marca) {
  const [rows] = await db.execute(`
    SELECT id_vehiculo, marca, modelo, placa, chasis, anio, color, precio, estado
    FROM vehiculos
    WHERE LOWER(marca) LIKE LOWER(?)
    ORDER BY id_vehiculo
  `, [`%${marca}%`]);
  return rows;
}

async function buscarPorPlaca(placa) {
  const [rows] = await db.execute(
    'SELECT * FROM vehiculos WHERE placa = ?',
    [placa]
  );
  return rows[0] || null;
}

async function buscarPorChasis(chasis) {
  const [rows] = await db.execute(
    'SELECT * FROM vehiculos WHERE chasis = ?',
    [chasis]
  );
  return rows[0] || null;
}

async function crear(vehiculo) {
  const [result] = await db.execute(`
    INSERT INTO vehiculos
      (marca, modelo, placa, chasis, anio, color, precio, estado)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
  `, [
    vehiculo.marca,
    vehiculo.modelo,
    vehiculo.placa,
    vehiculo.chasis,
    vehiculo.anio,
    vehiculo.color || null,
    vehiculo.precio,
    vehiculo.estado
  ]);

  return buscarPorId(result.insertId);
}

async function actualizar(id, vehiculo) {
  await db.execute(`
    UPDATE vehiculos
    SET marca = ?, modelo = ?, placa = ?, chasis = ?, anio = ?,
        color = ?, precio = ?, estado = ?
    WHERE id_vehiculo = ?
  `, [
    vehiculo.marca,
    vehiculo.modelo,
    vehiculo.placa,
    vehiculo.chasis,
    vehiculo.anio,
    vehiculo.color || null,
    vehiculo.precio,
    vehiculo.estado,
    id
  ]);

  return buscarPorId(id);
}

async function eliminar(id) {
  const [result] = await db.execute(
    'DELETE FROM vehiculos WHERE id_vehiculo = ?',
    [id]
  );
  return result.affectedRows > 0;
}

module.exports = {
  listarTodos,
  buscarPorId,
  buscarPorMarca,
  buscarPorPlaca,
  buscarPorChasis,
  crear,
  actualizar,
  eliminar
};
