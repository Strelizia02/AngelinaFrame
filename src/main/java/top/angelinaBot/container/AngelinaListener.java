package top.angelinaBot.container;

import org.springframework.stereotype.Service;

import top.angelinaBot.model.MessageInfo;

public abstract class AngelinaListener {
    private String qq;
    private String groupId;
    private Integer second = 60;
    public final long timestamp = System.currentTimeMillis();

    public String className;

    /**
     * 基础的监听器构造方法，能够监听全部群聊消息
     */
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
    
     /**
     * 增强的监听器构造方法，只监听当前群号的方法
     */
    public AngelinaListener(String groupId) {
        this.groupId = groupId;
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

    /**
     * 抽象callback方法，用于对监听消息的判定
     * 返回值boolean就是是否符合你想要监听的消息
     */
    public abstract boolean callback(MessageInfo message);
    
     /**
     * callback的增强方法，除callback本身的实现外，引入了对群号的判定
     */
    public boolean callbackUp(MessageInfo message) {
        if (groupId == null) {
            return callback(message);
        } else {
            return groupId.equals(message.getGroupId()) && callback(message);
        }
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }
}
