require('dotenv').config();

const fs = require('fs');
const path = require('path');
const http = require('http');
const express = require('express');
const soap = require('soap');
const db = require('./config/db');
const vehiculoSoapService = require('./soap/vehiculoSoapService');

const app = express();
const PORT = Number(process.env.PORT || 8000);
const SOAP_PATH = '/soap/vehiculos';

app.get('/', (req, res) => {
  res.type('html').send(`
    <h1>Venta Vehículos SOAP</h1>
    <p>WSDL: <a href="${SOAP_PATH}?wsdl">${SOAP_PATH}?wsdl</a></p>
    <p>XSD: <a href="/contracts/vehiculos.xsd">/contracts/vehiculos.xsd</a></p>
    <p>Health: <a href="/health">/health</a></p>
  `);
});

app.get('/health', async (req, res) => {
  try {
    await db.query('SELECT 1');
    res.json({ ok: true, database: 'connected' });
  } catch (error) {
    res.status(500).json({ ok: false, database: 'error', message: error.message });
  }
});

app.get('/contracts/vehiculos.xsd', (req, res) => {
  res.type('application/xml').sendFile(
    path.join(__dirname, '..', 'contracts', 'vehiculos.xsd')
  );
});

const wsdlPath = path.join(__dirname, '..', 'contracts', 'vehiculos.wsdl');
const wsdlXml = fs.readFileSync(wsdlPath, 'utf8');

const server = http.createServer(app);

soap.listen(server, SOAP_PATH, vehiculoSoapService, wsdlXml, () => {
  console.log('Servicio SOAP registrado correctamente');
});

server.listen(PORT, () => {
  console.log(`Servidor:       http://localhost:${PORT}`);
  console.log(`SOAP:           http://localhost:${PORT}${SOAP_PATH}`);
  console.log(`WSDL:           http://localhost:${PORT}${SOAP_PATH}?wsdl`);
  console.log(`XSD:            http://localhost:${PORT}/contracts/vehiculos.xsd`);
  console.log(`Health MySQL:   http://localhost:${PORT}/health`);
});

process.on('SIGINT', async () => {
  console.log('\nCerrando servidor...');
  await db.end();
  server.close(() => process.exit(0));
});