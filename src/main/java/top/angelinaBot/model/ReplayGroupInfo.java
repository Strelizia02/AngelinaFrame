package top.angelinaBot.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 回复消息结构化Bean
 **/
public class ReplayGroupInfo {
    //登录QQ
    Long loginQQ;
    //qq
    Long qq;
    //昵称
    String name;
    //群号
    Long groupId;
    //文字内容
    String replayMessage;
    //图片内容
    List<BufferedImage> replayImg = new ArrayList<>();
    //踢出群
    String kick;
    //禁言
    Integer muted;
    //戳一戳
    Boolean isNudged = false;

    public ReplayGroupInfo(MessageInfo messageInfo) {
        this.loginQQ = messageInfo.getLoginQq();
        this.groupId = messageInfo.getGroupId();
        this.qq = messageInfo.getQq();
        this.name = messageInfo.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLoginQQ() {
        return loginQQ;
    }

    public void setLoginQQ(Long loginQQ) {
        this.loginQQ = loginQQ;
    }

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

    public String getKick() {
        return kick;
    }

    public void setKick(String kick) {
        this.kick = kick;
    }

    public Integer getMuted() {
        return muted;
    }

    public void setMuted(Integer muted) {
        this.muted = muted;
    }

    public Boolean getNudged() {
        return isNudged;
    }

    public void setNudged(Boolean nudged) {
        isNudged = nudged;
    }

    public String getReplayMessage() {
        return replayMessage;
    }

    public void setReplayMessage(String replayMessage) {
        this.replayMessage = replayMessage;
    }

    public List<BufferedImage> getReplayImg() {
        return replayImg;
    }

    public void setReplayImg(List<BufferedImage> replayImg) {
        this.replayImg = replayImg;
    }
}
