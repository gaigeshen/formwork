package work.gaigeshen.formwork.basal.amqp;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RabbitMessageReceiver implements MessageReceiver {

    private static final Logger log = LoggerFactory.getLogger(RabbitMessageReceiver.class);

    private final MessageProcessors processors;

    public RabbitMessageReceiver(List<MessageProcessor> processors) {
        if (Objects.isNull(processors)) {
            throw new IllegalArgumentException("message processors cannot be null");
        }
        this.processors = new MessageProcessors(processors);
    }

    @Override
    public void receive(String message, String messageType) throws Exception {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message cannot be null");
        }
        processors.process(message, messageType);
    }

    @RabbitListener
    public void handleMessage(Message<String> message, AmqpMessageHeaderAccessor headers, Channel channel) {
        Object messageType = headers.getHeader("messageType");
        String payload = message.getPayload();
        try {
            receive(payload, (String) messageType);
            channel.basicAck(headers.getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                channel.basicNack(headers.getDeliveryTag(), false, true);
            } catch (IOException ex) {
            }
        }
    }
}
