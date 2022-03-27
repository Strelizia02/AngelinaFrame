package top.angelinaBot.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
public class AngelinaAspect {
    //用于存放所有被注解的方法
    public static final Map<String, Method> keyWordsMap = new HashMap<>();

    @Resource(name = "opq")
    SendMessageUtil sendMessageUtil;

    @Pointcut("@annotation(top.angelinaBot.annotation.Angelina)")
    public void annotationPointcut() {

    }

    @Around("annotationPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ReplayInfo proceed = (ReplayInfo) joinPoint.proceed();
        //获取切面，在切入点之后把返回值通过qq框架发送
        sendMessageUtil.sendGroupMsg(proceed);
        return proceed;
    }
}
