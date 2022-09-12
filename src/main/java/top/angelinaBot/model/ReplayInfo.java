package top.angelinaBot.model;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
    List<ExternalResource> replayImg = new ArrayList<>();
    //语音文件
    File mp3;
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
    public List<ExternalResource> getReplayImg() {
        return replayImg;
    }

    /**
     * 以BufferImage格式插入图片
     * @param bufferedImage 图片BufferedImage
     */
    public void setReplayImg(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, "jpg", os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            ExternalResource externalResource = ExternalResource.create(inputStream);
            replayImg.add(externalResource);
            inputStream.close();
        } catch (IOException e) {
            log.error("BufferImage读取IO流失败");
        }
    }

    /**
     * 以文件格式插入图片
     * @param file 文件File
     */
    public void setReplayImg(File file) {
        try (InputStream inputStream = new FileInputStream(file)){
            ExternalResource externalResource = ExternalResource.create(inputStream);
            replayImg.add(externalResource);
        } catch (IOException e) {
            log.error("File读取IO流失败");
        }
    }

    /**
     * 以url形式插入图片
     * @param url 图片url
     */
    public void setReplayImg(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection httpUrl = (HttpURLConnection) u.openConnection();
            httpUrl.connect();
            try (InputStream is = httpUrl.getInputStream()){
                ExternalResource externalResource = ExternalResource.create(is);
                replayImg.add(externalResource);
            }
        } catch (IOException e) {
            log.error("读取图片URL失败");
        }
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
        return "ReplayInfo{" +
                "loginQQ=" + loginQQ +
                ", qq=" + qq +
                ", name='" + name + '\'' +
                ", groupId=" + groupId +
                ", replayMessage='" + replayMessage + '\'' +
                ", replayImg=" + replayImg +
                ", mp3=" + mp3 +
                ", kick='" + kick + '\'' +
                ", muted=" + muted +
                ", isNudged=" + isNudged +
                '}';
    }
}
