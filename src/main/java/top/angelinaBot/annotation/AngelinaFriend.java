package top.angelinaBot.annotation;

import java.lang.annotation.*;

/**
 * Angelina框架的私聊方法通用修饰注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AngelinaFriend {
    //关键字触发
    String[] keyWords();
}
