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
     * 定义队列和延迟交换机
     *
     * @author gaigeshen
     */
    @Configuration
    static class DeclareConfiguration {

        @Value("${spring.rabbitmq.listener.queue}")
        private String receiveQueue;

        @Value("${spring.rabbitmq.template.exchange.delay}")
        private String delayExchange;

        @Bean
        public Queue receiveQueue() {
            return QueueBuilder.durable(receiveQueue).build();
        }

        @Bean
        public Exchange delayExchange() {
            return ExchangeBuilder.directExchange(delayExchange).delayed().build();
        }

        @Bean
        public Binding delayExchangeBinding() {
            return BindingBuilder.bind(receiveQueue()).to(delayExchange()).with("*").noargs();
        }
    }
}
