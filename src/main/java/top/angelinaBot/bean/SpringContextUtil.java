package top.angelinaBot.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //重写ApplicationContextAware获取Spring运行时Context
        SpringContextUtil.applicationContext = applicationContext;
    }


    public static Object getBean(String name) throws BeansException{
        //根据class名获取Bean
        return applicationContext.getBean(name);
    }
}
