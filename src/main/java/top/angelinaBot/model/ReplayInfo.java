package top.angelinaBot.model;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
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
    Long groupId;
    //文字内容
    String replayMessage;
    //图片内容
    List<InputStream> replayImg = new ArrayList<>();
    //踢出群
    String kick;
    //禁言
    Integer muted;
    //戳一戳
    Boolean isNudged = false;

    public ReplayInfo(MessageInfo messageInfo) {
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

    /**
     * 获取ReplayInfo的图片集合
     * @return 返回图片的输入流集合
     */
    public List<InputStream> getReplayImg() {
        return replayImg;
    }

    /**
     * 以BufferImage格式插入图片
     * @param bufferedImage 图片BufferedImage
     */
    public void setReplayImg(BufferedImage bufferedImage) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", os);
            replayImg.add(new ByteArrayInputStream(os.toByteArray()));
        } catch (IOException e) {
            log.error("BufferImage读取IO流失败");
        }
    }

    /**
     * 以文件格式插入图片
     * @param file 文件File
     */
    public void setReplayImg(File file) {
        try {
            replayImg.add(new FileInputStream(file));
        } catch (IOException e) {
            log.error("File读取IO流失败");
        }
    }
}
