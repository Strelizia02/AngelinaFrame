package top.angelinaBot.container;

import org.springframework.stereotype.Component;
import top.angelinaBot.model.EventEnum;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 存放所有@Angelina注解的方法映射。
 **/
@Component
public class AngelinaContainer {
    //用于存放所有被注解的方法
    public static final Map<String, Method> groupMap = new HashMap<>();

    public static final Map<String, List<String>> chatMap = new HashMap<>();

    public static final Map<String, Method> dHashMap = new HashMap<>();

    public static final Map<String, Method> friendMap = new HashMap<>();

    public static final Map<EventEnum, Method> eventMap = new HashMap<>();
}
