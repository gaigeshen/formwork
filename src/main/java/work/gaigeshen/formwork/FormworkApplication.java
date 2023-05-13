package work.gaigeshen.formwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import work.gaigeshen.formwork.message.MessageSender;

import java.time.Duration;
import java.util.Collections;

/**
 * @author gaigeshen
 */
@SpringBootApplication
public class FormworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormworkApplication.class, args);
    }

    @Autowired
    private MessageSender messageSender;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            messageSender.sendDelayMessage("queue.default", "这是测试的延迟消息", Collections.emptyMap(), Duration.ofSeconds(20));
        };
    }
}
