package ec.edu.uta.venta_vehiculos_api.service;

import ec.edu.uta.venta_vehiculos_api.dto.NotificacionVehiculoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificacionProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    public void enviarNotificacion(Long vehiculoId, String tipo, String mensaje) {
        NotificacionVehiculoDTO notificacion = new NotificacionVehiculoDTO(vehiculoId, tipo, mensaje);
        rabbitTemplate.convertAndSend(queueName, notificacion);
    }
}