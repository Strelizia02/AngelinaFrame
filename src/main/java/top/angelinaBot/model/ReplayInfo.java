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
    /**
     * 登录QQ
     */
    String loginQQ;
    /**
     * qq
     */
    String qq;
    /**
     * 昵称
     */
    String name;
    /**
     * 群号
     */
    List<String> groupId = new ArrayList<>();
    /**
     * 回复的消息id
     */
    String messageId;
    /**
     * 文字内容
     */
    String replayMessage;
    /**
     * 图片内容
     */
    List<Object> replayImg = new ArrayList<>();
    /**
     * 语音文件
     */
    File mp3;
    /**
     * 踢出群
     */
    String kick;
    /**
     * 禁言
     */
    Integer muted;
    /**
     * 戳一戳
     */
    Boolean isNudged = false;

    /**
     * 所用框架
     */
    String frame;

    public ReplayInfo(MessageInfo messageInfo) {
        this.loginQQ = messageInfo.getLoginQq();
        this.setGroupId(messageInfo.getGroupId());
        this.qq = messageInfo.getQq();
        this.name = messageInfo.getName();
        this.messageId = messageInfo.getMessageId();
        this.frame = messageInfo.getFrame();
    }

    public ReplayInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginQQ() {
        return loginQQ;
    }

    public void setLoginQQ(String loginQQ) {
        this.loginQQ = loginQQ;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public List<String> getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId.add(groupId);
    }

    public void setGroupId(Collection<String> groupIds) {
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
        if (bufferedImage == null) {
            log.warn("BufferImage图片不存在");
            return;
        }
        replayImg.add(bufferedImage);
    }

    /**
     * 以文件格式插入图片
     * @param file 文件File
     */
    public void setReplayImg(File file) {
        if (file == null) {
            log.warn("文件图片不存在");
            return;
        }
        replayImg.add(file);
    }

    /**
     * 以url形式插入图片
     * @param url 图片url
     */
    public void setReplayImg(String url) {
        if (url == null || url.equals("")) {
            log.warn("url图片不存在");
            return;
        }
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

    public String getFrame() {
        return frame;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
