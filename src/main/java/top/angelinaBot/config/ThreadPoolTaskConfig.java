package top.angelinaBot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 配置线程池相关信息
 **/
@Configuration
public class ThreadPoolTaskConfig {
    /**
     * 线程池配置信息，线程池主要用于发送消息.
     * @return
     */
    @Bean("taskModuleExecutor")
    public Executor taskModuleExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds(300);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setThreadNamePrefix("Angelina");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
