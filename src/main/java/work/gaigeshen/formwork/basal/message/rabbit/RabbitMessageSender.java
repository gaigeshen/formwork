package work.gaigeshen.formwork.basal.message.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.message.MessageSender;
import work.gaigeshen.formwork.basal.message.MessageSendingException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * 消息发送器实现
 *
 * @author gaigeshen
 */
public class RabbitMessageSender implements MessageSender {

    private final RabbitTemplate rabbitTemplate;

    private final String delayExchange;

    public RabbitMessageSender(RabbitTemplate rabbitTemplate, String delayExchange) {
        if (Objects.isNull(rabbitTemplate)) {
            throw new IllegalArgumentException("rabbitTemplate cannot be null");
        }
        this.rabbitTemplate = rabbitTemplate;
        this.delayExchange = delayExchange;
    }

    @Override
    public void sendMessage(String queue, String message, Map<String, Object> headers) throws MessageSendingException {
        if (Objects.isNull(queue) || Objects.isNull(message)) {
            throw new IllegalArgumentException("queue and message cannot be null");
        }
        MessageBuilder builder = MessageBuilder.withBody(message.getBytes(StandardCharsets.UTF_8));
        if (Objects.nonNull(headers)) {
            headers.forEach(builder::setHeaderIfAbsent);
        }
        Message builded = builder.setContentType("text/plain").build();
        CorrelationData correlationData = new CorrelationData(IdentityGenerator.generateDefault());
        try {
            rabbitTemplate.send(queue, builded, correlationData);
        } catch (Exception e) {
            throw new MessageSendingException("could not send message: " + message, e);
        }
    }

    @Override
    public void sendDelayMessage(String queue, String message, Map<String, Object> headers, Duration duration) throws MessageSendingException {
        if (Objects.isNull(queue) || Objects.isNull(message)) {
            throw new IllegalArgumentException("queue and message cannot be null");
        }
        if (Objects.isNull(delayExchange)) {
            throw new MessageSendingException("missing delay exchange: " + message);
        }
        MessageBuilder builder = MessageBuilder.withBody(message.getBytes(StandardCharsets.UTF_8));
        if (Objects.nonNull(headers)) {
            headers.forEach(builder::setHeaderIfAbsent);
        }
        Message builded = builder.setContentType("text/plain").setHeader("x-delay", duration.toMillis()).build();
        CorrelationData correlationData = new CorrelationData(IdentityGenerator.generateDefault());
        try {
            rabbitTemplate.send(delayExchange, queue, builded, correlationData);
        } catch (Exception e) {
            throw new MessageSendingException("could not send message: " + message, e);
        }
    }
}
