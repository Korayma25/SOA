const amqp = require('amqplib');

const RABBITMQ_URL = process.env.RABBITMQ_URL || 'amqp://guest:guest@localhost:5672';
const COLA = process.env.RABBITMQ_QUEUE || 'notificaciones.vehiculos';
const DESTINATARIO = process.env.NOTIF_DESTINATARIO || 'usuario@correo.com';

let canalPromise = null;

// Abre la conexión y el canal una sola vez y los reutiliza.
function obtenerCanal() {
  if (canalPromise) return canalPromise;

  canalPromise = (async () => {
    const conexion = await amqp.connect(RABBITMQ_URL);
    const reiniciar = () => { canalPromise = null; };
    conexion.on('error', (err) => console.error('[RabbitMQ] error:', err.message));
    conexion.on('close', reiniciar);

    const canal = await conexion.createChannel();
    await canal.assertQueue(COLA, { durable: true });
    return canal;
  })();

  // Si falla la conexión, el próximo intento vuelve a probar.
  canalPromise.catch(() => { canalPromise = null; });
  return canalPromise;
}

// tipo: REGISTRO | ACTUALIZACION | ELIMINACION
async function publicarEvento(tipo, vehiculoId, mensaje) {
  const evento = {
    vehiculoId: Number(vehiculoId),
    destinatario: DESTINATARIO,
    tipo,
    mensaje
  };

  const canal = await obtenerCanal();
  canal.sendToQueue(COLA, Buffer.from(JSON.stringify(evento)), {
    persistent: true,
    contentType: 'application/json'
  });

  return evento;
}

module.exports = { publicarEvento };