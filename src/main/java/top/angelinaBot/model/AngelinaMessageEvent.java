package top.angelinaBot.model;

public class AngelinaMessageEvent {
    private Object lock = new Object();
    private MessageInfo messageInfo;

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }
}
