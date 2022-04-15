package top.angelinaBot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import top.angelinaBot.service.TestService;

/**
 * 方法重写注入配置类
 */
@Component
public class AngelinaBeanConfig {

    /**
     * 当用户需要重写框架中的某段代码时，重写代码后通过@Bean注入覆盖掉原有实例
     */
    @Bean("helloWorldService")
    public TestService getHelloWorldService() {
        return new TestService();
    }
}
