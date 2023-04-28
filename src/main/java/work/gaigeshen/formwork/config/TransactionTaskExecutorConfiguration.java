package work.gaigeshen.formwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import work.gaigeshen.formwork.commons.transaction.SpringTransactionTaskExecutor;
import work.gaigeshen.formwork.commons.transaction.TransactionTaskExecutor;

/**
 * 事务任务执行器配置
 *
 * @author gaigeshen
 */
@Configuration
public class TransactionTaskExecutorConfiguration {

    @Bean
    public TransactionTaskExecutor transactionTaskExecutor(PlatformTransactionManager transactionManager) {
        return new SpringTransactionTaskExecutor(transactionManager, asyncTaskExecutor());
    }

    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setKeepAliveSeconds(120);
        taskExecutor.setAwaitTerminationSeconds(600);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setThreadNamePrefix("tx-task-");
        taskExecutor.initialize();
        return taskExecutor;
    }

}
