package top.angelinaBot.annotation;

/**
 * Angelina框架的功能权限注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    PermissionEnum permission() default PermissionEnum.GroupAdministrator;
}
