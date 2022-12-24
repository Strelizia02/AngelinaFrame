package top.angelinaBot.container;

import org.springframework.stereotype.Component;
import top.angelinaBot.model.EventEnum;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 存放所有@Angelina注解的方法映射的容器。
 **/
@Component
public class AngelinaContainer {
    /**
     所有群聊触发关键字和方法名称的映射
     */
    public static final Map<String, String> groupFuncNameMap = new HashMap();
    
    /**
     * 群聊的关键字触发方法
     */
    public static final Map<String, Method> groupMap = new HashMap<>();
    /**
     * 闲聊的关键字触发方法，闲聊配置仅能在群聊中触发
     */
    public static final Map<String, List<String>> chatMap = new HashMap<>();
    /**
     * dHash图片触发方法，仅能在群聊中触发
     */
    public static final Map<String, Method> dHashMap = new HashMap<>();
    /**
     * 私聊的关键字触发方法
     */
    public static final Map<String, Method> friendMap = new HashMap<>();
    /**
     * 事件触发方法
     */
    public static final Map<EventEnum, Method> eventMap = new HashMap<>();
    /**
     * 超级管理员用户
     */
    public static final Set<String> administrators = new HashSet<>();
}
