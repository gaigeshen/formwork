package work.gaigeshen.formwork.basal.amqp.rabbit;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;
import org.springframework.messaging.Message;
import work.gaigeshen.formwork.basal.amqp.MessageProcessingException;
import work.gaigeshen.formwork.basal.amqp.MessageProcessors;

import java.io.IOException;
import java.util.Objects;

/**
 * 消息监听器
 *
 * @author gaigeshen
 */
public class RabbitMessageListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMessageListener.class);

    private final MessageProcessors processors;

    /**
     * 创建消息监听器
     *
     * @param processors 消息处理器集合
     */
    public RabbitMessageListener(MessageProcessors processors) {
        if (Objects.isNull(processors)) {
            throw new IllegalArgumentException("message processors cannot be null");
        }
        this.processors = processors;
    }

    @RabbitListener
    public void handleMessage(Message<String> message, AmqpMessageHeaderAccessor headers, Channel channel) {
        Long deliveryTag = headers.getDeliveryTag();
        try {
            processors.process(message.getPayload(), headers.toMap());
            channel.basicAck(deliveryTag, false);
        }
        catch (MessageProcessingException ex) {
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException iex) {
                log.warn("error nack message: [ " + message + " ]", iex);
            }
        }
        catch (IOException ex) {
            log.warn("error ack message: [ " + message + " ]", ex);
        }
    }
}
