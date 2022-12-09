package top.angelinaBot.model;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 回复消息结构化Bean
 **/
@Slf4j
public class ReplayInfo {
    //登录QQ
    Long loginQQ;
    //qq
    Long qq;
    //昵称
    String name;
    //群号
    List<Long> groupId = new ArrayList<>();
    //文字内容
    String replayMessage;
    //图片内容
    List<Object> replayImg = new ArrayList<>();
    //语音文件
    File mp3;
    //踢出群
    String kick;
    //禁言
    Integer muted;
    //戳一戳
    Boolean isNudged = false;

    String channel;

    String bot;

    String author;

    public ReplayInfo(MessageInfo messageInfo) {
        this.loginQQ = messageInfo.getLoginQq();
        this.setGroupId(messageInfo.getGroupId());
        this.qq = messageInfo.getQq();
        this.name = messageInfo.getName();
        this.channel = messageInfo.getChannelId();
        this.bot = messageInfo.getBot();
        this.author = messageInfo.getAuthor();
    }

    public ReplayInfo() {

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

    public List<Long> getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId.add(groupId);
    }

    public void setGroupId(Collection<Long> groupIds) {
        this.groupId.addAll(groupIds);
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

    public File getMp3() {
        return mp3;
    }

    public void setMp3(File mp3) {
        this.mp3 = mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = new File(mp3);
    }

    public void setReplayMessage(String replayMessage) {
        this.replayMessage = replayMessage;
    }

    /**
     * 获取ReplayInfo的图片集合
     * @return 返回图片的输入流集合
     */
    public List<Object> getReplayImg() {
        return replayImg;
    }

    /**
     * 以BufferImage格式插入图片
     * @param bufferedImage 图片BufferedImage
     */
    public void setReplayImg(BufferedImage bufferedImage) {
        replayImg.add(bufferedImage);
    }

    /**
     * 以文件格式插入图片
     * @param file 文件File
     */
    public void setReplayImg(File file) {
        replayImg.add(file);
    }

    /**
     * 以url形式插入图片
     * @param url 图片url
     */
    public void setReplayImg(String url) {
        replayImg.add(url);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(replayMessage);
        if (replayImg.size() > 0) {
            sb.append("[图片]");
        }
        if (mp3 != null) {
            sb.append("[语音]");
        }

        if (muted != null) {
            sb.append("[禁言").append(muted).append("秒]");
        }

        if (kick != null) {
            sb.append("[踢出群聊]");
        }
        return sb.toString();
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getBot() {
        return bot;
    }

    public void setBot(String bot) {
        this.bot = bot;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
