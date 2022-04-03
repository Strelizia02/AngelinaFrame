package top.angelinaBot.Aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 存放所有@Angelina注解的方法映射，保留方法切点。
 **/
@Aspect
@Component
public class AngelinaAspect {
    //用于存放所有被注解的方法
    public static final Map<String, Method> keyWordsMap = new HashMap<>();

    public static final Map<String, Method> dHashMap = new HashMap<>();
}
