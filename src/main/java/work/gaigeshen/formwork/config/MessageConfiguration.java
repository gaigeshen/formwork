package work.gaigeshen.formwork.config;

import org.springframework.amqp.core.*;
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
    public MessageSender messageSender(RabbitTemplate rabbitTemplate, Exchange delayExchange) {
        return new RabbitMessageSender(rabbitTemplate, delayExchange.getName());
    }

    /**
     * 定义队列和延迟交换机，延迟交换机绑定定义的队列，路由键为队列的名称
     *
     * @author gaigeshen
     */
    @Configuration
    static class DeclareConfiguration {

        @Bean
        public Binding delayExchangeBinding(Queue receiveQueue, Exchange delayExchange) {
            return BindingBuilder.bind(receiveQueue).to(delayExchange).with(receiveQueue.getName()).noargs();
        }

        @Bean
        public Queue receiveQueue(@Value("${spring.rabbitmq.listener.queue}") String receiveQueue) {
            return QueueBuilder.durable(receiveQueue).build();
        }

        @Bean
        public Exchange delayExchange(@Value("${spring.rabbitmq.template.exchange.delay}") String delayExchange) {
            return ExchangeBuilder.directExchange(delayExchange).delayed().build();
        }
    }
}
