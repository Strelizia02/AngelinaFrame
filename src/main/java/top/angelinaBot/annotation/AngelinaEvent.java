package top.angelinaBot.annotation;

import java.lang.annotation.*;

/**
 * Angelina框架的群聊方法通用修饰注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AngelinaEvent {
    //关键字触发
    String[] keyWords();
    //图片DHash触发，需要循环比对，尽量减少DHash
    String[] dHash() default "";
}
