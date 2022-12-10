package top.angelinaBot.annotation;

import top.angelinaBot.model.FunctionType;
import top.angelinaBot.model.PermissionEnum;

import java.lang.annotation.*;

import static top.angelinaBot.model.FunctionType.Others;

/**
 * Angelina框架的群聊方法通用修饰注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AngelinaGroup {
    //关键字触发
    String[] keyWords();
    //最低运行权限
    PermissionEnum permission() default PermissionEnum.GroupUser;
    //图片DHash触发，需要循环比对，尽量减少DHash
    String[] dHash() default "";
    //使用方法描述
    String description() default "";
    //功能块归属
    FunctionType funcClass();
    //作者
    String author() default "Strelitzia02";
}
