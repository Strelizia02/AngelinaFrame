package top.angelinaBot.bean;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import top.angelinaBot.annotation.ReflectCallMethodAngelina;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 实现SmartInitializingSingleton接口，SpringBoot加载完所有Bean以后的回调这个接口。
 **/
@Component
public class AngelinaInitialization implements SmartInitializingSingleton {

    /**
     * 该方法仅在加载完所有的Bean以后，Spring完全启动前执行一次
     */
    @Override
    public void afterSingletonsInstantiated() {
        ReflectCallMethodAngelina.annotationMethod();
        WebSocketConfig.webSocketListener();
    }
}
