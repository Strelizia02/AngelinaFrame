package top.angelinaBot.container;

import top.angelinaBot.model.MessageInfo;

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
                    Method method = c.getMethod(s.getMethodName());
                    if (method.getAnnotation(AngelinaGtoup.class)!= null) {
                        className = s.getClassName();
                        break;
                    }
                } catch (NoSuchMethodException | ClassNotFoundException e) {
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
