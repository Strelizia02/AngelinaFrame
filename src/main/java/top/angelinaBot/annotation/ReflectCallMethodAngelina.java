package top.angelinaBot.annotation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;
import top.angelinaBot.Aspect.AngelinaAspect;
import top.angelinaBot.Exception.AngelinaException;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;


@Slf4j
public class ReflectCallMethodAngelina {

    /**
     * 全量扫描service底下的所有接口查找每一个被@Angelina注解的方法。
     * 1.加载所有参数到到KeyWordsMap中
     * 2.检查keyWords是否有重复
     * 3.检查方法参数是否符合要求
     */
    public static void annotationMethod() {

        //查找service包下，所有Service类
        Reflections reflections = new Reflections("top.angelinaBot.service");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Service.class);

        for (Class<?> clazz : typesAnnotatedWith) {
            //循环查找被Angelina修饰的方法
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, Angelina.class);
            for (Method method : methods) {
                if (method.getReturnType() != ReplayInfo.class) {
                    throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 的返回值类型应为 ReplayInfo.class");
                } else {
                    //获取方法参数
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 的参数个数应为 1 个");
                    } else {
                        if (parameterTypes[0] != MessageInfo.class) {
                            throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 参数类型不对，正确参数类型应为 top.angelinaBot.model.MessageInfo");
                        } else {
                            for (Annotation annotation : method.getDeclaredAnnotations()) {
                                //获取方法上的所有修饰注解，循环找到Angelina注解
                                if (annotation instanceof Angelina) {
                                    String[] keyWords = ((Angelina) annotation).keyWords();
                                    for (String keyWord : keyWords) {
                                        //判断关键字是否重复
                                        if (AngelinaAspect.keyWordsMap.containsKey(keyWord)) {
                                            Method replaceMethod = AngelinaAspect.keyWordsMap.get(keyWord);
                                            throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 关键字 \"" + keyWord + "\" 与 " + replaceMethod.getDeclaringClass().getName() + " 的方法 " + replaceMethod.getName() + "() 关键字 \"" + keyWord + "\" 重复");
                                        } else {
                                            //关闭安全检查提升反射速度
                                            method.setAccessible(true);
                                            //确认完全符合要求后，将关键字和方法添加至全局变量keyWordsMap中
                                            AngelinaAspect.keyWordsMap.put(keyWord, method);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

