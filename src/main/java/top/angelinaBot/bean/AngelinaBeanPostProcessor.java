package top.angelinaBot.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.angelinaBot.Exception.AngelinaException;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.aspect.AngelinaAspect;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Bean加载完成的后置接口
 */
@Component
@Slf4j
public class AngelinaBeanPostProcessor implements BeanPostProcessor {

    /**
     * 全量扫描所有Bean，查找每一个被@AngelinaGroup注解的方法。
     * 1.加载所有参数到到KeyWordsMap中
     * 2.检查keyWords是否有重复
     * 3.检查方法参数是否符合要求
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        // 循环查找被AngelinaGroup修饰的方法,AngelinaGroup不可切面，切面后会被CGLIB代理，代理类里就没有注解信息了
        List<Method> groupMethods = MethodUtils.getMethodsListWithAnnotation(clazz, AngelinaGroup.class);
        for (Method method: groupMethods) {
            if (method.getReturnType() != ReplayInfo.class) {
                throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 的返回值类型应为 ReplayGroupInfo.class");
            } else {
                // 获取方法参数
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 的参数个数应为 1 个");
                } else {
                    if (parameterTypes[0] != MessageInfo.class) {
                        throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 参数类型不对，正确参数类型应为 MessageInfo.class");
                    } else {
                        for (Annotation annotation : method.getDeclaredAnnotations()) {
                            //获取方法上的所有修饰注解，循环找到Angelina注解
                            if (annotation instanceof AngelinaGroup) {
                                //获取文字关键字
                                String[] groupKeyWords = ((AngelinaGroup) annotation).keyWords();
                                for (String keyWord : groupKeyWords) {
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

                                String[] dHashList = ((AngelinaGroup) annotation).dHash();
                                if (AngelinaAspect.dHashMap.size() == 10) {
                                    log.error("dHash 方法超过十个,会一定程度影响性能");
                                }
                                for (String dHash : dHashList) {
                                    //判断DHash是否重复
                                    if (!"".equals(dHash) && AngelinaAspect.dHashMap.containsKey(dHash)) {
                                        Method replaceMethod = AngelinaAspect.dHashMap.get(dHash);
                                        throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() DHash与 " + replaceMethod.getDeclaringClass().getName() + " 的方法 " + replaceMethod.getName() + "() DHash重复");
                                    } else {
                                        //关闭安全检查提升反射速度
                                        method.setAccessible(true);
                                        //确认完全符合要求后，将关键字和方法添加至全局变量dHashMap中
                                        AngelinaAspect.dHashMap.put(dHash, method);
                                    }
                                }
                            }
                            if (annotation instanceof AngelinaFriend) {
                                String[] friendKeyWords = ((AngelinaFriend) annotation).keyWords();
                                for (String keyWord : friendKeyWords) {
                                    //判断关键字是否重复
                                    if (AngelinaAspect.friendMap.containsKey(keyWord)) {
                                        Method replaceMethod = AngelinaAspect.friendMap.get(keyWord);
                                        throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 关键字 \"" + keyWord + "\" 与 " + replaceMethod.getDeclaringClass().getName() + " 的方法 " + replaceMethod.getName() + "() 关键字 \"" + keyWord + "\" 重复");
                                    } else {
                                        //关闭安全检查提升反射速度
                                        method.setAccessible(true);
                                        //确认完全符合要求后，将关键字和方法添加至全局变量friendMap中
                                        AngelinaAspect.friendMap.put(keyWord, method);
                                    }
                                }
                            }

                            if (annotation instanceof AngelinaEvent) {
                                EventEnum eventEnum = ((AngelinaEvent) annotation).event();
                                //判断关键字是否重复
                                if (AngelinaAspect.eventMap.containsKey(eventEnum)) {
                                    Method replaceMethod = AngelinaAspect.eventMap.get(eventEnum);
                                    throw new AngelinaException(clazz + " 的方法 " + method.getName() + "() 事件 \"" + eventEnum + "\" 与 " + replaceMethod.getDeclaringClass().getName() + " 的方法 " + replaceMethod.getName() + "() 事件 \"" + eventEnum + "\" 重复");
                                } else {
                                    //关闭安全检查提升反射速度
                                    method.setAccessible(true);
                                    //确认完全符合要求后，将关键字和方法添加至全局变量eventMap中
                                    AngelinaAspect.eventMap.put(eventEnum, method);
                                }

                            }
                        }
                    }
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
