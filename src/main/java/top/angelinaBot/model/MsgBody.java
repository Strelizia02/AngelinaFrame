package top.angelinaBot.model;

import java.io.Serializable;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * QQ消息原始Bean
 **/
public class MsgBody implements Serializable {
    private String text;
    private Long qq;
    private String name;
    private Long groupId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }
}
