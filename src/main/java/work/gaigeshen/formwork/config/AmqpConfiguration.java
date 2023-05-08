package work.gaigeshen.formwork.config;

import com.rabbitmq.client.Channel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import work.gaigeshen.formwork.basal.amqp.RabbitMessageReceiver;
import work.gaigeshen.formwork.basal.amqp.MessageProcessor;
import work.gaigeshen.formwork.basal.amqp.MessageReceiver;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生产者投递消息失败抛出异常，消息序列化的格式配置
 *
 * @author gaigeshen
 */
@Configuration
public class AmqpConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AmqpConfiguration.class);

    private final List<MessageProcessor> processors;

    public AmqpConfiguration(List<MessageProcessor> processors) {
        this.processors = processors;
    }

    @Bean
    public MessageReceiver messageReceiver() {
        return new RabbitMessageReceiver(new ArrayList<>(processors));
    }

    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback(RabbitTemplate rabbitTemplate) {
        RabbitTemplate.ConfirmCallback callback = (correlationData, ack, cause) -> {
            log.info("correlationData: [{}], ack: [{}], cause: [{}]", correlationData, ack, cause);
            if (!ack) {
                throw new IllegalStateException("correlationData: [ " + correlationData + " ], cause: [ " + cause + " ]");
            }
        };
        rabbitTemplate.setConfirmCallback(callback);
        return callback;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue messageQueueOne() {
        return new Queue("routing.one");
    }

    @Bean
    public Queue messageQueueTwo() {
        return new Queue("routing.two");
    }

    @Bean
    public ApplicationRunner messageSendRunner1(RabbitTemplate rabbitTemplate) {
        return args -> {
            MessageData message = new MessageData();
            message.setName("Rose");
            message.setAge(32);
            message.setPrice(new BigDecimal("99.88"));
            message.setCreateTime(new Date());
            rabbitTemplate.convertAndSend("routing.one", message, new CorrelationData(IdentityGenerator.generateDefault()));
        };
    }

    @Bean
    public ApplicationRunner messageSendRunner2(RabbitTemplate rabbitTemplate) {
        return args -> {
            MessageData message = new MessageData();
            message.setName("Jack");
            message.setAge(32);
            message.setPrice(new BigDecimal("99.88"));
            message.setCreateTime(new Date());
            rabbitTemplate.convertAndSend("routing.two", message, new CorrelationData(IdentityGenerator.generateDefault()));
        };
    }

    @RabbitListener(queues = "routing.one")
    public void haneleMessage1(@Payload MessageData message,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                               Channel channel) {
        System.out.println("message from one: " + message);
        System.out.println("deliveryTag: " + deliveryTag);
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "routing.two")
    public void haneleMessage2(@Payload MessageData message,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                               Channel channel) {
        System.out.println("message from two: " + message);
        System.out.println("deliveryTag: " + deliveryTag);
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class MessageData {

        private String name;

        private Integer age;

        private BigDecimal price;

        private Date createTime;

    }
}
