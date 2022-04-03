package top.angelinaBot.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 实现ApplicationContextAware接口进而获得Spring容器
 **/
@Configuration
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 获取Spring运行时Context
     * @param applicationContext Spring核心容器
     * @throws BeansException 加载Bean失败时抛出BeanException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 根据名称获取applicationContext中的Bean
     * @param name Bean 默认名为类名首字母小写
     * @return 返回Bean实例
     * @throws BeansException 加载Bean失败时抛出BeanException
     */
    public static Object getBean(String name) throws BeansException{
        //根据class名获取Bean
        return applicationContext.getBean(name);
    }

    /**
     * 根据注解，获取Spring容器中所有包含该注解的Class
     * @param annotation 注解Class
     * @return Class的Set集合
     */
    public static Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation){
        Set<Class<?>> inClassList = new HashSet<>();
        Map<String, Object> beanList = applicationContext.getBeansWithAnnotation(annotation);
        beanList.forEach((k, v) -> inClassList.add(applicationContext.getType(k)));
        return inClassList;
    }
}
