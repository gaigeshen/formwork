package work.gaigeshen.formwork.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.basal.amqp.DefaultMessageProcessors;
import work.gaigeshen.formwork.basal.amqp.MessageProcessor;
import work.gaigeshen.formwork.basal.amqp.MessageProcessors;
import work.gaigeshen.formwork.basal.amqp.rabbit.RabbitMessageListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gaigeshen
 */
@Configuration
public class AmqpConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AmqpConfiguration.class);

    @Bean
    public MessageProcessors messageProcessors(ObjectProvider<MessageProcessor> processors) {
        List<MessageProcessor> orderedProcessors = processors.orderedStream().collect(Collectors.toList());
        return new DefaultMessageProcessors(orderedProcessors);
    }

    @Bean
    public RabbitMessageListener rabbitMessageListener(MessageProcessors processors) {
        return new RabbitMessageListener(processors);
    }

    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback(RabbitTemplate rabbitTemplate) {
        RabbitTemplate.ConfirmCallback callback = (correlationData, ack, cause) -> {
            if (!ack) {
                log.info("correlationData: [{}], cause: [{}]", correlationData, cause);
            }
        };
        rabbitTemplate.setConfirmCallback(callback);
        return callback;
    }
}
