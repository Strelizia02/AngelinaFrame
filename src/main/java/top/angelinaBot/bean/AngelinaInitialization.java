package top.angelinaBot.bean;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import top.angelinaBot.annotation.ReflectCallMethodAngelina;


@Component
public class AngelinaInitialization implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        //Spring完全加载完的回调方法，仅在加载完所有的bean，spring完全启动前执行一次
        ReflectCallMethodAngelina.annotationMethod();
    }
}
