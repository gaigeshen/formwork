package work.gaigeshen.formwork.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.message.*;
import work.gaigeshen.formwork.message.rabbit.RabbitMessageReceiver;
import work.gaigeshen.formwork.message.rabbit.RabbitMessageSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息接收和发送配置
 *
 * @author gaigeshen
 */
@Configuration
public class MessageConfiguration {

    @Value("${spring.rabbitmq.listener.queue}")
    private String receiveQueue;

    @Bean
    public Queue receiveQueue() {
        return new Queue(receiveQueue, true, false, false);
    }

    @Bean
    public MessageProcessors messageProcessors(ObjectProvider<MessageProcessor> processors) {
        List<MessageProcessor> orderedProcessors = processors.orderedStream().collect(Collectors.toList());
        if (!orderedProcessors.isEmpty()) {
            return new DefaultMessageProcessors(orderedProcessors);
        }
        return new DefaultMessageProcessors(Collections.singletonList(new DefaultMessageProcessor()));
    }

    @Bean
    public MessageReceiver messageReceiver(MessageProcessors messageProcessors) {
        return new RabbitMessageReceiver(messageProcessors);
    }

    @Bean
    public MessageSender messageSender(RabbitTemplate rabbitTemplate) {
        return new RabbitMessageSender(rabbitTemplate);
    }
}
