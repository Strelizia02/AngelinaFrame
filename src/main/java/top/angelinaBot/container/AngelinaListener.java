package top.angelinaBot.container;

import top.angelinaBot.model.MessageInfo;

public abstract class AngelinaListener {
    private Long qq;
    private Long groupId;
    private Integer second = 60;
    public final long timestamp = System.currentTimeMillis();

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
    
    public Integer getSecond() { return second; }

    public void setSecond(Integer second) {
        if(second>0) this.second = second;
        else this.second = 60;
    }
}
