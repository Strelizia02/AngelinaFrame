package top.angelinaBot.container;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;

import java.lang.reflect.Method;

public abstract class AngelinaListener {
    private Long qq;
    private Long groupId;
    private Integer second = 60;
    public final long timestamp = System.currentTimeMillis();

    public String className;

    public AngelinaListener() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement s: stackTrace) {
                try {
                    Class<?> c = Class.forName(s.getClassName());
                    if (c.getAnnotation(Service.class) != null) {
                        className = s.getClassName();
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    className = Thread.currentThread().getStackTrace()[3].getClassName();
                    e.printStackTrace();
                }
            }
    }

    public abstract boolean callback(MessageInfo message);

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }
}
