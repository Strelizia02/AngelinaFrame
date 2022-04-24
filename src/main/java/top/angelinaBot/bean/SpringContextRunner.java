package top.angelinaBot.bean;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;


/**
 * @author strelitzia
 * @Date 2022/04/03
 * 实现ApplicationContextAware接口进而获得Spring容器
 **/
@Configuration
public class SpringContextRunner implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 获取Spring运行时Context
     * @param applicationContext Spring核心容器
     * @throws BeansException 加载Bean失败时抛出BeanException
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringContextRunner.applicationContext = applicationContext;
    }

    /**
     * 根据名称获取applicationContext中的Bean
     * @param name Bean 默认名为类名首字母小写
     * @return 返回Bean实例
     * @throws BeansException 加载Bean失败时抛出BeanException
     */
    public static Object getBean(Class<?> name) throws BeansException {
        //根据class名获取Bean
        return applicationContext.getBean(name);
    }
}
