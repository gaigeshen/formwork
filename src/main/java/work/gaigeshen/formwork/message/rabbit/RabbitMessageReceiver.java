package work.gaigeshen.formwork.message.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;
import org.springframework.messaging.Message;
import work.gaigeshen.formwork.message.MessageProcessors;
import work.gaigeshen.formwork.message.MessageReceiver;
import work.gaigeshen.formwork.message.MessageReceivingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消息接收器实现
 *
 * @author gaigeshen
 */
public class RabbitMessageReceiver implements MessageReceiver {

    private final MessageProcessors processors;

    /**
     * 创建消息接收器
     *
     * @param processors 消息处理器集合
     */
    public RabbitMessageReceiver(MessageProcessors processors) {
        if (Objects.isNull(processors)) {
            throw new IllegalArgumentException("message processors cannot be null");
        }
        this.processors = processors;
    }

    @Override
    public void receiveMessage(String message, Map<String, Object> headers) throws MessageReceivingException {
        try {
            processors.process(message, headers);
        } catch (Exception e) {
            throw new MessageReceivingException("could not process message: " + message, e);
        }
    }

    // 接收指定队列的所有消息交给消息处理器进行后续处理
    // 处理消息时发生任何异常将消息重新入队
    // 确认或者拒绝消息时的异常将被忽略
    @RabbitListener(queues = "${spring.rabbitmq.listener.queue}")
    public void listenMessage(Message<String> message, AmqpMessageHeaderAccessor headers, Channel channel) {
        try {
            receiveMessage(message.getPayload(), new HashMap<>(headers.toMap()));
        } catch (Exception e) {
            RabbitChannels.basicRejectAndRequeue(message, channel, headers.getDeliveryTag());
            return;
        }
        RabbitChannels.basicAck(message, channel, headers.getDeliveryTag());
    }
}
