package top.angelinaBot.model;

import java.awt.image.BufferedImage;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 回复消息结构化Bean
 **/
public class ReplayInfo {
    String replayMessage;
    BufferedImage replayImg;

    public String getReplayMessage() {
        return replayMessage;
    }

    public void setReplayMessage(String replayMessage) {
        this.replayMessage = replayMessage;
    }

    public BufferedImage getReplayImg() {
        return replayImg;
    }

    public void setReplayImg(BufferedImage replayImg) {
        this.replayImg = replayImg;
    }

}
