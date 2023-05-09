package work.gaigeshen.formwork.message.rabbit;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import java.io.IOException;

/**
 *
 * @author gaigeshen
 */
public abstract class RabbitChannels {

    private static final Logger log = LoggerFactory.getLogger(RabbitChannels.class);

    private RabbitChannels() { }

    public static void basicAck(Message<String> message, Channel channel, long deliveryTag) {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.warn("could not ack message: " + message, e);
        }
    }

    public static void basicRejectAndRequeue(Message<String> message, Channel channel, long deliveryTag) {
        try {
            channel.basicReject(deliveryTag, true);
        } catch (IOException e) {
            log.warn("could not reject message: " + message, e);
        }
    }
}
